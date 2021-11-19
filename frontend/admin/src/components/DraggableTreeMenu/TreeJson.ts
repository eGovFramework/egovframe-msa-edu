import {
  ItemId,
  TreeData,
  TreeDestinationPosition,
  TreeItem,
  TreeSourcePosition,
} from '@atlaskit/tree'
import { IMenuTree } from '@service'
import produce from 'immer'

export class TreeJson {
  private readonly treeJson: IMenuTree[]
  private readonly treeData: Record<ItemId, TreeItem>
  private readonly source: TreeSourcePosition
  private readonly destination: TreeDestinationPosition

  constructor(
    tree: TreeData,
    source: TreeSourcePosition,
    destination: TreeDestinationPosition,
  ) {
    this.treeJson = []
    this.treeData = tree.items
    this.source = source
    this.destination = destination
  }

  convert(): IMenuTree[] {
    if (this.source.parentId === this.destination.parentId) {
      if (this.source.parentId === '0') {
        this.updateTopLevel()
        return this.treeJson
      }

      this.update(this.source.parentId)
      return this.treeJson
    }

    this.update(this.source.parentId)
    this.update(this.destination.parentId)

    return this.treeJson
  }

  private updateTopLevel() {
    const children = this.treeData['0'].children

    children.map((item, idx) => {
      let toplevel = this.treeData[item]
      this.treeJson.push(
        produce(toplevel.data as IMenuTree, draft => {
          draft.sortSeq = idx + 1
        }),
      )
    })
  }

  private update(parentId: ItemId) {
    let parent = this.treeData[parentId]

    let children: IMenuTree[] = []

    const menuId = parent.data?.menuId
    parent.children.map((item, idx) => {
      let data = this.treeData[item].data as IMenuTree
      let child = produce(data, draft => {
        draft.sortSeq = idx + 1
        draft.parentId = menuId
      })
      children.push(child)
    })

    if (parent.data) {
      this.treeJson.push(
        produce(parent.data as IMenuTree, draft => {
          draft.children = children
        }),
      )
    }
  }
}
