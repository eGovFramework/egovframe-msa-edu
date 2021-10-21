import {
  checkedChildren,
  findAllIds,
  findTreeItem,
  treeChecked,
  treeTargetChecked,
} from '@components/DraggableTreeMenu/TreeUtils'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown'
import ArrowRightIcon from '@material-ui/icons/ArrowRight'
import TreeView, { TreeViewProps } from '@material-ui/lab/TreeView'
import produce from 'immer'
import React, {
  forwardRef,
  useEffect,
  useImperativeHandle,
  useState,
} from 'react'
import CustomTreeItem from './CustomTreeItem'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
    },
  }),
)

export type CustomTreeViewType = {
  getTreeData: () => any[]
  handleAllChecked: (checked: boolean) => void
}

interface CustomTreeViewProps {
  data: any[]
  isChecked?: boolean
  isAllExpanded?: boolean
  treeViewProps?: TreeViewProps
}

const renderTree = (
  nodes: any,
  isChecked: boolean,
  handleChecked: (node: object, checked: boolean) => void,
) => {
  return (
    <CustomTreeItem
      key={`item-${nodes.id}`}
      nodeId={`${nodes.id}`}
      node={nodes}
      isChecked={isChecked}
      handleChecked={handleChecked}
    >
      {Array.isArray(nodes.children)
        ? nodes.children.map((node, idx) =>
            renderTree(node, isChecked, handleChecked),
          )
        : null}
    </CustomTreeItem>
  )
}

const CustomTreeView = forwardRef<CustomTreeViewType, CustomTreeViewProps>(
  (props, ref) => {
    const { data, isChecked = false, isAllExpanded, treeViewProps } = props
    const classes = useStyles()

    const [expanded, setExpanded] = useState<string[]>(null)
    const [tree, setTree] = useState(data)

    useEffect(() => {
      if (data) {
        setTree(data)
      }
    }, [data])

    useEffect(() => {
      if (isAllExpanded) {
        const ids: string[] = findAllIds(tree)
        setExpanded(ids)
      } else {
        setExpanded([])
      }
    }, [isAllExpanded])

    const handleNodeToggle = (event: object, nodeIds: []) => {
      setExpanded(nodeIds)
    }

    const handleChecked = (node: any, checked: boolean) => {
      // 해당 노드와 자식노드들 chekced 상태 변경
      const updateTree = (item: any, isChild: boolean = false) => {
        return produce(item, draft => {
          if (isChild || `${item.id}` === `${node.id}`) {
            draft.isChecked = checked

            if (draft.children) {
              const arr = Array.from(draft.children)
              draft.children = arr.map(i => {
                return updateTree(i, true)
              })
            }
          } else {
            if (draft.children) {
              const arr = Array.from(draft.children)
              draft.children = arr.map(i => {
                return updateTree(i, false)
              })
            }
          }
        })
      }

      let newTree = tree.map(item => {
        return updateTree(item)
      }) as any[]

      if (checked) {
        //checked = true 이면 부모 node checked
        let findItem = { ...node }
        while (true) {
          const find = findTreeItem(newTree, findItem.id, 'id')

          if (!find.parent) {
            break
          }

          newTree = newTree.map(item => {
            return treeTargetChecked(item, find.parent, checked)
          }) as any[]

          findItem = { ...find.parent }
        }
      } else {
        //checked = false 이면  level==1 인 부모 node는 자식 node 들 중 체크가 하나라도 있으면 그냥 넘어가고 하나도 없으면 체크 해제
        let findItem = { ...node }
        if (findItem.level > 1) {
          let level = 0
          while (level !== 1) {
            const find = findTreeItem(newTree, findItem.id, 'id')

            level = find.parent.level
            findItem = { ...find.parent }
          }

          const childrenCheck = checkedChildren(findItem)
          if (!childrenCheck) {
            newTree = newTree.map(item => {
              return treeTargetChecked(item, findItem, false)
            }) as any[]
          }
        }
      }

      setTree(newTree)
    }

    useImperativeHandle(ref, () => ({
      getTreeData: () => {
        return tree
      },
      handleAllChecked: (checked: boolean) => {
        const newTree = tree.map(item => {
          return treeChecked(item, checked)
        })

        setTree(newTree)
      },
    }))

    return (
      <TreeView
        className={classes.root}
        defaultCollapseIcon={<ArrowDropDownIcon />}
        defaultExpandIcon={<ArrowRightIcon />}
        defaultEndIcon={<div style={{ width: 24 }} />}
        expanded={expanded}
        onNodeToggle={handleNodeToggle}
        {...treeViewProps}
      >
        {tree && tree.map(item => renderTree(item, isChecked, handleChecked))}
      </TreeView>
    )
  },
)

export default CustomTreeView
