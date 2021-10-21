import React, { createRef, useEffect, useState } from 'react'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import { ItemId, TreeItem } from '@atlaskit/tree'
import IconButton from '@material-ui/core/IconButton'
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown'
import ArrowRightIcon from '@material-ui/icons/ArrowRight'
import Icon from '@material-ui/core/Icon'
import Typography from '@material-ui/core/Typography'
import { useRecoilState } from 'recoil'
import { treeChangeNameAtom, draggableTreeSelectedAtom } from '@stores'
import TextField from '@material-ui/core/TextField'
import { ClassNameMap } from '@material-ui/styles'

export const useTreeItemStyles = makeStyles((theme: Theme) =>
  createStyles({
    labelRoot: {
      display: 'flex',
      alignItems: 'center',
      padding: theme.spacing(0.5, 0),
    },
    label: {
      display: 'flex',
      color: theme.palette.text.secondary,
    },
    selected: {
      color: '#1a73e8',
    },
    labelIcon: {
      marginRight: theme.spacing(1),
    },
    labelText: {
      fontWeight: 'inherit',
      flexGrow: 1,
      textAlign: 'initial',
    },
    bull: {
      width: '1em',
      height: '1em',
      fontSize: '1.5rem',
      marginRight: theme.spacing(1),
      paddingLeft: theme.spacing(1),
    },
  }),
)

const getIcon = (
  item: TreeItem,
  onExpand: (itemId: ItemId) => void,
  onCollapse: (itemId: ItemId) => void,
  classes: ClassNameMap,
) => {
  if (item.children && item.children.length > 0) {
    return item.isExpanded ? (
      <IconButton
        size="small"
        onClick={() => onCollapse(item.id)}
        aria-label="collapse"
      >
        <ArrowDropDownIcon />
      </IconButton>
    ) : (
      <IconButton
        size="small"
        onClick={() => onExpand(item.id)}
        aria-label="expand"
      >
        <ArrowRightIcon />
      </IconButton>
    )
  }

  return (
    <IconButton className={classes.bull} size="small">
      &bull;
    </IconButton>
  )
}

const setLabel = (
  item: TreeItem,
  classes: ClassNameMap,
  handleClick: (event: React.MouseEvent<HTMLDivElement>) => void,
  handleKeyPress: (event: React.KeyboardEvent<HTMLInputElement>) => void,
  handleBlur: () => void,
  changed?: boolean,
  selected?: boolean,
  inputRef?: React.RefObject<HTMLInputElement>,
) => {
  if (changed) {
    return (
      <TextField
        size="small"
        id="tree-name"
        inputRef={inputRef}
        margin="dense"
        defaultValue={item.data?.name}
        onKeyPress={handleKeyPress}
        onBlur={handleBlur}
        autoFocus
      />
    )
  }

  return (
    <div
      onClick={handleClick}
      className={`${classes.label} ${selected ? classes.selected : ''}`}
    >
      <Icon fontSize="small" className={classes.labelIcon}>
        {item.data?.icon || 'folder'}
      </Icon>
      <Typography variant="body2" className={classes.labelText}>
        {item.data ? item.data.name : ''}
      </Typography>
    </div>
  )
}

export interface DraaggableTreeMenuItemProps {
  item: TreeItem
  onExpand: (itemId: ItemId) => void
  onCollapse: (itemId: ItemId) => void
  selected?: boolean
}

const DraaggableTreeMenuItem = (props: DraaggableTreeMenuItemProps) => {
  const classes = useTreeItemStyles()
  const { item, onExpand, onCollapse, selected } = props

  const [treeSelected, setTreeSelected] = useRecoilState(
    draggableTreeSelectedAtom,
  )
  const [treeChangeName, setTreeChangeName] = useRecoilState(treeChangeNameAtom)
  const [changed, setChanged] = useState<boolean>(false)
  const nameRef = createRef<HTMLInputElement>()

  useEffect(() => {
    if (
      treeChangeName.state === 'change' &&
      item.data?.menuId === treeSelected?.menuId
    ) {
      setChanged(true)
      return
    }

    setChanged(false)
  }, [treeSelected, item, treeChangeName])

  const handleClick = (event: React.MouseEvent<HTMLDivElement>) => {
    event.preventDefault()
    setTreeSelected(item.data)
  }

  const handleKeyPress = e => {
    if (e.key === 'Enter') {
      setTreeChangeName({
        state: 'complete',
        id: item.data?.menuId,
        name: nameRef.current?.value,
      })
    }
  }

  const handleBlur = () => {
    setTreeChangeName({
      state: 'none',
      id: null,
      name: null,
    })
  }

  return (
    <div className={`${classes.labelRoot}`}>
      {getIcon(item, onExpand, onCollapse, classes)}
      {setLabel(
        item,
        classes,
        handleClick,
        handleKeyPress,
        handleBlur,
        changed,
        selected,
        nameRef,
      )}
    </div>
  )
}

export default DraaggableTreeMenuItem
