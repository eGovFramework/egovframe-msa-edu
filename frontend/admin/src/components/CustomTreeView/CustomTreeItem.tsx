import { useTreeItemStyles } from '@components/DraggableTreeMenu/DraaggableTreeMenuItem'
import Checkbox from '@material-ui/core/Checkbox'
import Icon from '@material-ui/core/Icon'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Typography from '@material-ui/core/Typography'
import TreeItem, { TreeItemProps } from '@material-ui/lab/TreeItem'
import React, { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      color: theme.palette.text.secondary,
      '&:hover': {
        backgroundColor: theme.palette.action.hover,
      },
    },
    checked: {
      padding: theme.spacing(0.5),
    },
  }),
)

interface CustomTreeItemProps extends TreeItemProps {
  node: any
  isChecked?: boolean
  handleChecked?: (node: any, checked: boolean) => void
}

const CustomTreeItem = (props: CustomTreeItemProps) => {
  const { node, isChecked, handleChecked, ...rest } = props
  const classes = useStyles()
  const itemClasses = useTreeItemStyles()

  const { i18n } = useTranslation()

  const [checked, setChecked] = useState<boolean>(node.isChecked)

  useEffect(() => {
    if (node) {
      setChecked(node.isChecked)
    }
  }, [node])

  const handleLabelClick = (event: React.MouseEvent<HTMLInputElement>) => {
    event.preventDefault()
    handleChecked(node, !checked)
  }

  return (
    <TreeItem
      label={
        <div
          className={`${itemClasses.labelRoot} ${
            checked ? itemClasses.selected : ''
          }`}
        >
          {isChecked && (
            <Checkbox
              className={classes.checked}
              checked={checked}
              color="primary"
              size="small"
              inputProps={{ 'aria-label': 'primary checkbox' }}
            />
          )}
          <Icon fontSize="small" className={itemClasses.labelIcon}>
            {node.icon || 'folder'}
          </Icon>
          <Typography variant="body2" className={itemClasses.labelText}>
            {i18n.language === 'ko' ? node.korName : node.engName}
          </Typography>
        </div>
      }
      onLabelClick={handleLabelClick}
      classes={{
        root: classes.root,
        label: itemClasses.label,
      }}
      {...rest}
    />
  )
}

export default CustomTreeItem
