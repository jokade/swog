package scala.scalanative.interop

import scala.annotation.tailrec
import scala.scalanative.libc.stdlib
import scala.scalanative.unsafe.{CSize, Ptr, Zone}

trait PoolZone extends Zone {
  def statInfo: String
  def ref(): Unit
  def unref(): Unit
  def totalSize: CSize
}

object PoolZone {
  var defaultBlockSize: CSize = 4096
  var defaultAllocThreshold: CSize = 256
  var defaultCollectStats: Boolean = false

  lazy val allocator: PoolZone = new PoolZoneImpl(defaultAllocThreshold,defaultBlockSize,defaultCollectStats)

  def apply[T](f: Zone => T): T = {
    allocator.ref()
    try f(allocator)
    finally allocator.unref()
  }

  final class PoolZoneImpl(nodeAllocThreshold: CSize, smallBlockSize: CSize, collectStats: Boolean) extends PoolZone {
    assert(nodeAllocThreshold<=smallBlockSize,"smallBlockSize must be >= nodeAllocThreshold")
    private var _level = 0

    private var _maxLevel = 0
    private var _numSmallAllocs = 0
    private var _numNodeAllocs = 0
    private var _maxSmallAllocSize: CSize = 0
    private var _maxNodeAllocSize: CSize = 0
    private var _totalSmallAllocSize: CSize = 0
    private var _totalNodeAllocSize: CSize = 0
    private var _minNodeAllocSize: CSize = 0
    private var _numBlockAllocs = 0
    private var _usedBlockSize: CSize = smallBlockSize
    private var _maxUsedBlockSize: CSize = smallBlockSize

    private var _freeBlocks: Block = null
    private var _blocks = allocBlock(null)
    private var _nodes: Node = null

    override def totalSize: CSize = _nodes match {
      case null => smallBlockSize
      case n => smallBlockSize + n.totalSize(0)
    }

    override def statInfo: String =
      s"""PoolZone Statistics:
         |-------------------
         |
         |        small block size: ${smallBlockSize} bytes
         |    node alloc threshold: ${nodeAllocThreshold} bytes
         |
         |              max. level: ${_maxLevel}
         |
         |          # small allocs: ${_numSmallAllocs}
         |  total small alloc size: ${_totalSmallAllocSize} bytes
         |   max. small alloc size: ${_maxSmallAllocSize} bytes
         |   mean small alloc size: ${if(_numSmallAllocs==0) 0 else _totalSmallAllocSize / _numSmallAllocs} bytes
         |
         |            # new blocks: ${_numBlockAllocs}
         |    total size of blocks: ${_maxUsedBlockSize} bytes
         |
         |           # node allocs: ${_numNodeAllocs}
         |   total node alloc size: ${_totalNodeAllocSize} bytes
         |    max. node alloc size: ${_maxNodeAllocSize} bytes
         |    min. node alloc size: ${_minNodeAllocSize} bytes
         |    mean node alloc size: ${if(_numNodeAllocs==0) 0 else _totalNodeAllocSize / _numNodeAllocs} bytes
       """.stripMargin

    override def ref(): Unit = {
      _level += 1
      if(collectStats || _level > _maxLevel)
        _maxLevel = _level
    }

    override def unref(): Unit =
      if(_level==0)
        throw new RuntimeException("cannot unref() PoolZone: level already 0!")
      else {
        _level -= 1
        freeNodes()
        freeBlocks()
        if(_nodes != null)
          throw new RuntimeException(s"inconsistent node PoolZone: node alloc pool should be zero at level=0, got: ${_nodes}")
      }

    override def alloc(size: CSize): Ptr[Byte] =
      if(size <= nodeAllocThreshold)
        allocSmall(size)
      else
        allocNode(size)


    override def close(): Unit = ???

    override def isClosed: Boolean = _level <= 0

    private def allocSmall(size: CSize): Ptr[Byte] = {
      if(size > _blocks.freeSize)
        _blocks = allocBlock(_blocks)
      if(collectStats) {
        _numSmallAllocs += 1
        _totalSmallAllocSize += size
        if(size > _maxSmallAllocSize)
          _maxSmallAllocSize = size
      }
      val p = _blocks.nextPtr
      _blocks.nextPtr += size
      _blocks.freeSize -= size
      p
    }

    private def allocNode(size: CSize): Ptr[Byte] = {
      if(collectStats) {
        _numNodeAllocs += 1
        _totalNodeAllocSize += size
        if(size > _maxNodeAllocSize)
          _maxNodeAllocSize = size
        if(_minNodeAllocSize == 0)
          _minNodeAllocSize = size
        else if(size < _minNodeAllocSize)
          _minNodeAllocSize = size
      }
      _nodes = Node(_level,stdlib.malloc(size),size,_nodes)
      _nodes.ptr
    }

    private def allocBlock(nextBlock: Block): Block = {
      val newBlock =
        if(_freeBlocks != null) {
          val block = _freeBlocks
          _freeBlocks = _freeBlocks.nextBlock
          block.nextBlock = nextBlock
          block
        }
        else {
          if(collectStats) {
            _numBlockAllocs += 1
          }
          Block(_level,smallBlockSize,nextBlock)
        }
      if(collectStats) {
        _usedBlockSize += smallBlockSize
        if(_usedBlockSize >= _maxUsedBlockSize)
          _maxUsedBlockSize = _usedBlockSize
      }
      newBlock
    }

    private def freeBlocks(): Unit = {
      while(_blocks.nextBlock != null && (_blocks.level > _level || _level ==0)) {
        val free = _blocks
        free.nextBlock = _freeBlocks
        free.reset()
        _blocks = free.nextBlock
        if(collectStats)
          _usedBlockSize -= smallBlockSize
        _blocks = _blocks.nextBlock
      }
      if(_level == 0)
        _blocks.reset()
    }

    private def freeNodes(): Unit = if(_nodes!=null) {
      var node = _nodes
      while(node != null && node.level > _level) {
        stdlib.free(node.ptr)
        node = node.next
      }
      _nodes = node
    }
  }

  private case class Node(level: Int, ptr: Ptr[Byte], size: CSize, next: Node) {
    @tailrec
    final def totalSize(prevSize: CSize): CSize = next match {
      case null => size + prevSize
      case tail => tail.totalSize(size + prevSize)
    }
  }

  private class Block(val level: Int, val ptr: Ptr[Byte], val size: CSize, var nextPtr: Ptr[Byte], var freeSize: CSize, var nextBlock: Block) {
    def free(): Unit = stdlib.free(ptr)
    def reset(): Unit = {
      nextPtr = ptr
      freeSize = size
    }
  }
  private object Block {
    def apply(level: Int, size: CSize, next: Block): Block = {
      val ptr = stdlib.malloc(size)
      new Block(level,ptr,size,ptr,size,next)
    }
  }
}
