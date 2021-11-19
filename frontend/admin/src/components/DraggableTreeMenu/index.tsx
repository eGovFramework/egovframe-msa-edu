import Tree, {
  ItemId,
  moveItemOnTree,
  mutateTree,
  RenderItemParams,
  TreeData,
  TreeDestinationPosition,
  TreeSourcePosition,
} from '@atlaskit/tree'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import { IMenuTree } from '@service'
import { draggableTreeExpandedAtom, draggableTreeSelectedAtom } from '@stores'
import React, { useEffect, useState } from 'react'
import { useRecoilState, useRecoilValue } from 'recoil'
import DraaggableTreeMenuItem from './DraaggableTreeMenuItem'
import { TreeJson } from './TreeJson'
import { convertJsonToTreeData } from './TreeUtils'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      marginBottom: theme.spacing(1),
    },
    item: {
      color: theme.palette.text.secondary,
      '&:hover': {
        backgroundColor: theme.palette.action.hover,
      },
    },
    selected: {
      backgroundColor: `#e8f0fe`,
      color: '#1a73e8',
    },
  }),
)

const PADDING_PER_LEVEL = 40

export interface DraggableTreeMenuProps {
  data: IMenuTree[]
  handleTreeDnD: (tree: IMenuTree[]) => void
}

function DraggableTreeMenu(props: DraggableTreeMenuProps) {
  const classes = useStyles()
  const { data, handleTreeDnD } = props

  const treeSelected = useRecoilValue(draggableTreeSelectedAtom)
  const [treeExpanded, setTreeExpanded] = useRecoilState(
    draggableTreeExpandedAtom,
  )

  const [tree, setTree] = useState<TreeData>(null)

  useEffect(() => {
    setTreeExpanded('collapse')
  }, [])

  useEffect(() => {
    if (data) {
      setTree(convertJsonToTreeData(data))
    }
  }, [data])

  useEffect(() => {
    if (treeExpanded === 'none') {
      return
    }

    if (!tree) {
      return
    }

    const expanded = treeExpanded === 'expand'

    let treeData = tree
    for (const key in tree.items) {
      if (Object.prototype.hasOwnProperty.call(tree.items, key)) {
        treeData = mutateTree(treeData, key, { isExpanded: expanded })
      }
    }
    setTree(treeData)
  }, [treeExpanded])

  const renderItem = ({
    item,
    onExpand,
    onCollapse,
    provided,
  }: RenderItemParams) => {
    const selected: boolean = item.data?.menuId === treeSelected?.menuId

    return (
      <div
        className={`${classes.item} ${selected ? classes.selected : ''}`}
        ref={provided.innerRef}
        {...provided.draggableProps}
        {...provided.dragHandleProps}
      >
        <DraaggableTreeMenuItem
          item={item}
          onExpand={onExpand}
          onCollapse={onCollapse}
          selected={selected}
        />
      </div>
    )
  }

  const onExpand = (itemId: ItemId) => {
    setTreeExpanded('none')
    setTree(mutateTree(tree, itemId, { isExpanded: true }))
  }

  const onCollapse = (itemId: ItemId) => {
    setTreeExpanded('none')
    setTree(mutateTree(tree, itemId, { isExpanded: false }))
  }

  const onDragEnd = async (
    source: TreeSourcePosition,
    destination?: TreeDestinationPosition,
  ) => {
    if (!destination) {
      return
    }

    const newTree = moveItemOnTree(tree, source, destination)

    const treeJson = new TreeJson(newTree, source, destination)
    const convertTree = treeJson.convert()
    handleTreeDnD(convertTree)

    setTree(newTree)
  }

  return (
    <div className={classes.root}>
      {tree && (
        <Tree
          tree={tree}
          renderItem={renderItem}
          onExpand={onExpand}
          onCollapse={onCollapse}
          onDragEnd={onDragEnd}
          offsetPerLevel={PADDING_PER_LEVEL}
          isDragEnabled
          isNestingEnabled
        />
      )}
    </div>
  )
}

export default DraggableTreeMenu
