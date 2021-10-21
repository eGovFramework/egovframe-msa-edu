import React from 'react'
import { List } from '@material-ui/core'
import { makeStyles, createStyles, Theme } from '@material-ui/core/styles'
import MenuItem from './MenuItem'
import { useRecoilValue } from 'recoil'
import { menuStateAtom } from '@stores'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      listStyle: 'none',
      position: 'unset',
    },
  }),
)

export interface IMenuProps {
  open: boolean
}

const Menu = ({ open }: IMenuProps) => {
  const classes = useStyles()
  const menus = useRecoilValue(menuStateAtom)

  return (
    <List component="nav" className={classes.root}>
      {menus
        .filter(item => item.isShow)
        .map(item => (
          <MenuItem key={`menu-item-${item.id}`} {...item} drawerOpen={open} />
        ))}
    </List>
  )
}

export { Menu }
