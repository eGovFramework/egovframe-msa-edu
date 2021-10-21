import Collapse from '@material-ui/core/Collapse'
import Icon from '@material-ui/core/Icon'
import List from '@material-ui/core/List'
import ListItem from '@material-ui/core/ListItem'
import ListItemIcon from '@material-ui/core/ListItemIcon'
import ListItemText from '@material-ui/core/ListItemText'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import ExpandLess from '@material-ui/icons/ExpandLess'
import ExpandMore from '@material-ui/icons/ExpandMore'
import { currentMenuStateAtom, ISideMenu } from '@stores'
import theme from '@styles/theme'
import { useRouter } from 'next/router'
import React, { useCallback, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useRecoilValue } from 'recoil'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      '& .MuiListItemIcon-root': {
        minWidth: '36px',
      },
    },
    menuItem: {
      borderBottom: '1px solid #edf1f7',
      display: 'flex',
      overflow: 'hidden',
      width: 'auto',
      transition: 'all 300ms linear',
      position: 'relative',
      backgroundColor: 'transparent',
    },
    active: {
      color: theme.palette.primary.main,
    },
  }),
)

export interface IMenuItemProps extends ISideMenu {
  drawerOpen: boolean
}

/**
 * @TODO
 * 3단계 이상 그려지는 메뉴 처리
 */

const MenuItem: React.FC<IMenuItemProps> = props => {
  const { expanded, drawerOpen } = props
  const classes = useStyles()
  const router = useRouter()
  const { i18n } = useTranslation()

  const current = useRecoilValue(currentMenuStateAtom)

  const [open, setOpen] = useState<boolean>(expanded || false)

  const onClick = (item: ISideMenu) => {
    if (item.children.filter(i => i.isShow).length > 0) {
      setOpen(!open)
    } else {
      router.push(item.urlPath)
    }
  }

  const drawItem = useCallback(
    (item: ISideMenu) => {
      const active =
        current?.id === item.id
          ? true
          : item.children?.findIndex(ele => ele.id === current?.id) > -1
          ? true
          : false

      return (
        <div key={`list-item-div-${item.id}`} className={classes.root}>
          <ListItem
            button
            key={`list-item-${item.id}`}
            onClick={() => onClick(item)}
            className={`${classes.menuItem} ${active ? classes.active : null}`}
            style={{
              paddingLeft: theme.spacing(
                item.level * (item.level === 1 ? 3 : 2),
              ),
            }}
          >
            <ListItemIcon className={active ? classes.active : null}>
              <Icon> {item.icon || 'folder'}</Icon>
            </ListItemIcon>

            {drawerOpen && (
              <ListItemText
                key={`item-text-${item.id}`}
                primary={i18n.language === 'ko' ? item.korName : item.engName}
              />
            )}

            {drawerOpen &&
              item.children.filter(i => i.isShow).length > 0 &&
              (open ? <ExpandLess /> : <ExpandMore />)}
          </ListItem>
          {item.children.filter(i => i.isShow).length > 0 ? (
            <Collapse in={open && drawerOpen} timeout="auto" unmountOnExit>
              <List component="div" disablePadding>
                {item.children.filter(i => i.isShow).map(i => drawItem(i))}
              </List>
            </Collapse>
          ) : null}
        </div>
      )
    },
    [props, open],
  )

  return <>{drawItem(props)}</>
}

export default MenuItem
