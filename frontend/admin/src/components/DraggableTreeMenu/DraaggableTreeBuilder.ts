import { ItemId, TreeItem } from '@atlaskit/tree'
import { IMenuTree } from '@service'
import produce from 'immer'

export type TreeItemType = TreeItem & {
  parentId?: number
}

export default class TreeBuilder {
  rootId: ItemId

  items: Record<ItemId, TreeItemType>

  constructor(rootId: ItemId, data?: IMenuTree) {
    const rootItem = this._createItem(`${rootId}`, data)
    this.rootId = rootItem.id
    this.items = {
      [rootItem.id]: rootItem,
    }
  }

  withLeaf(item: IMenuTree) {
    const leafItem = this._createItem(`${this.rootId}-${item.menuId}`, item)
    this._addItemToRoot(leafItem.id)
    this.items[leafItem.id] = leafItem
    return this
  }

  withSubTree(tree: TreeBuilder) {
    const subTree = tree.build()
    this._addItemToRoot(`${this.rootId}-${subTree.rootId}`)

    Object.keys(subTree.items).forEach(itemId => {
      const finalId = `${this.rootId}-${itemId}`
      this.items[finalId] = {
        ...subTree.items[itemId],
        id: finalId,
        children: subTree.items[itemId].children.map(
          i => `${this.rootId}-${i}`,
        ),
      }
    })

    return this
  }

  build() {
    return {
      rootId: this.rootId,
      items: this.items,
    }
  }

  _addItemToRoot(id: string) {
    const rootItem = this.items[this.rootId]
    rootItem.children.push(id)
    rootItem.isExpanded = true
    rootItem.hasChildren = true
  }

  _createItem = (id: string, data?: IMenuTree) => {
    data = produce(data, draft => {
      if (draft) {
        draft.children = []
      }
    })

    return {
      id: `${id}`,
      children: [],
      hasChildren: false,
      isExpanded: false,
      isChildrenLoading: false,
      data,
      parentId: data?.parentId,
    }
  }
}
