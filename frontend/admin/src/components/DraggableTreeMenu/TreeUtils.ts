import { TreeData, TreeItem } from '@atlaskit/tree'
import { IMenuTree } from '@service'
import produce from 'immer'
import DraggableTreeBuilder from './DraaggableTreeBuilder'

/**
 * hierarchy json data -> atlaskit flat tree data
 *
 * @param data
 * @returns
 */
export const convertJsonToTreeData = (data: IMenuTree[]) => {
  const createTree = (item: any, builder: DraggableTreeBuilder) => {
    if (item.children) {
      let sub = new DraggableTreeBuilder(item.menuId, item)
      item.children.map(i => createTree(i, sub))
      builder.withSubTree(sub)
    } else {
      builder.withLeaf(item)
    }
  }

  let root = new DraggableTreeBuilder(0)
  data?.map(item => {
    createTree(item, root)
  })

  return root.build()
}

/**
 * atlaskit flat tree data ->  hierarchy json data
 *
 * @param tree
 * @returns
 */
export const convertTreeDataToJson = (tree: TreeData) => {
  let newTreeItem: TreeItem[] = []

  const arrTree = Object.values(tree.items)
  const root = arrTree.shift()

  root.children.map((itemId, index) => {
    const data = arrTree.splice(
      arrTree.findIndex(item => item.id === itemId),
      1,
    )
    data.map(item => {
      item.data = produce(item.data, draft => {
        draft.sortSeq = index + 1
        draft.parentId = null
      })
      newTreeItem.push(item)
    })
  })

  const convert = (target: TreeItem[], source: TreeItem[]) => {
    while (source.length > 0) {
      const data = source.shift()

      if (data.hasChildren) {
        target.push(
          produce(data, draft => {
            draft.hasChildren = false
          }),
        )
      }

      const idx = target.findIndex(item => item.children.includes(data.id))

      if (idx > -1) {
        const parent = produce(target[idx].data as IMenuTree, draft => {
          const childIdx = draft.children.findIndex(
            i => i.menuId === data.data.menuId,
          )

          const child = produce(data.data as IMenuTree, childDraft => {
            if (childIdx === -1) {
              childDraft.sortSeq = draft.children.length + 1
            }
            childDraft.parentId = draft.menuId
          })

          if (childIdx > -1) {
            draft.children[childIdx] = child
          } else {
            draft.children.push(child)
          }
        })

        target[idx] = produce(target[idx], draft => {
          draft.data = parent
        })
      }
    }

    return target
  }

  let target = newTreeItem.slice()
  let source = arrTree.slice()

  while (true) {
    newTreeItem = convert(target, source).slice()

    if (root.children.length === newTreeItem.length) {
      break
    }

    target = newTreeItem.filter(item => root.children.includes(item.id))
    source = newTreeItem.filter(item => !root.children.includes(item.id))
  }

  const newData: IMenuTree[] = []
  newTreeItem.map(treeItem => {
    newData.push(Object.assign(treeItem.data))
  })

  return newData
}

export interface IFindTree {
  item: any
  parent: any
}

/**
 * hierarchy json data에서 조건에 맞는 데이터 찾기
 *
 * @param arr  원본 json array (any[])
 * @param value 찾고자 하는 데이터 (number or string)
 * @param key object인 경우 데이터 key (string)
 * @returns
 */
export const findTreeItem = (
  arr: any[],
  value: number | string,
  key?: string,
): IFindTree => {
  let target
  let parent
  let findKey = key || 'index'
  const findAllItems = (item: any, parentItem?: any) => {
    if (item[findKey] === value) {
      target = item
      parent = parentItem
      return
    }

    if (item.children) {
      item.children.map((v, k) => {
        return findAllItems(v, item)
      })
    }
  }

  arr.map(item => {
    findAllItems(item)
  })

  return {
    item: target,
    parent,
  }
}

/**
 * hierarchy json data에서 모든 id값 찾기
 *
 * @param arr  원본 json array (any[])
 * @param findKey id의 key 값 = default 'id' (string)
 * @returns string[]
 */
export const findAllIds = (arr: any[], findKey?: string): string[] => {
  const ids = []
  const key = findKey || 'id'

  const findAll = (item: any) => {
    ids.push(`${item[key]}`)

    if (item.children) {
      item.children.map(i => findAll(i))
    }
  }

  arr.map(item => {
    findAll(item)
  })

  return ids
}

/**
 * hierarchy json data에서 해당하는 데이터 checked or unchecked
 *
 * @param node
 * @param target
 * @param checked
 * @returns
 */
export const treeTargetChecked = (node: any, target: any, checked: boolean) => {
  return produce(node, draft => {
    if (`${draft.id}` === `${target.id}`) {
      draft.isChecked = checked
    } else {
      if (draft.children) {
        const arr = Array.from(draft.children)
        draft.children = arr.map(i => {
          return treeTargetChecked(i, target, checked)
        })
      }
    }
  })
}

/**
 * 해당 노드 자식 데이터 모두 checked
 *
 * @param node
 * @returns
 */
export const checkedChildren = (node: any) => {
  for (const iterator of node.children) {
    if (iterator.isChecked) {
      return true
    }
  }

  return false
}

/**
 * tree data all checked or unchecked
 *
 * @param node
 * @param checked
 * @returns
 */
export const treeChecked = (node: any, checked: boolean) => {
  return produce(node, draft => {
    draft.isChecked = checked
    if (draft.children) {
      const arr = Array.from(draft.children)
      draft.children = arr.map(i => {
        return treeChecked(i, checked)
      })
    }
  })
}
