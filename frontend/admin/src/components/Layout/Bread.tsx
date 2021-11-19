import Breadcrumbs from '@material-ui/core/Breadcrumbs'
import Link from '@material-ui/core/Link'
import { makeStyles, Theme } from '@material-ui/core/styles'
import Typography from '@material-ui/core/Typography'
import { currentMenuStateAtom, ISideMenu, menuStateAtom } from '@stores'
import { useRouter } from 'next/router'
import React, { useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { useRecoilValue } from 'recoil'

const useStyles = makeStyles((theme: Theme) => ({
  root: {
    // marginBottom: theme.spacing(1),
  },
}))

const Bread: React.FC = () => {
  const classes = useStyles()
  const router = useRouter()
  const menus = useRecoilValue(menuStateAtom)
  const current = useRecoilValue(currentMenuStateAtom)
  const { i18n } = useTranslation()

  const findParent = useCallback(
    (menu: ISideMenu) => {
      let parent: ISideMenu
      const findItems = item => {
        if (item.id === menu.parentId) {
          parent = item
        }

        if (item.children) {
          item.children.map(v => {
            return findItems(v)
          })
        }
      }

      menus.map(item => {
        findItems(item)
      })
      return parent
    },
    [menus, current],
  )

  const hierarchy = useCallback(() => {
    if (!current) {
      return
    }

    if (current?.level === 1) {
      return (
        <Typography color="textPrimary">
          {i18n.language === 'ko' ? current?.korName : current?.engName}
        </Typography>
      )
    }

    let trees = []
    trees.push(current)
    let findMenu = current
    while (true) {
      let parent = findParent(findMenu)
      trees.push(parent)
      findMenu = parent
      if (parent.level === 1) {
        break
      }
    }

    let nodes = trees.reverse().map(item =>
      item.id === current.id ? (
        <Typography key={current.id} color="textPrimary">
          {i18n.language === 'ko' ? current?.korName : current?.engName}
        </Typography>
      ) : (
        <Link
          key={`brean-link-${item.id}`}
          color="inherit"
          href="/getting-started/installation/"
          onClick={(event: React.MouseEvent<HTMLAnchorElement, MouseEvent>) => {
            handleClick(event, item.urlPath)
          }}
        >
          {i18n.language === 'ko' ? item.korName : item.engName}
        </Link>
      ),
    )

    return nodes
  }, [current])

  const handleClick = (
    event: React.MouseEvent<HTMLAnchorElement, MouseEvent>,
    url: string,
  ) => {
    event.preventDefault()
    if (url) {
      router.push(url)
    }
  }
  return (
    <div className={classes.root}>
      <Breadcrumbs separator="›" aria-label="breadcrumb">
        <Link
          color="inherit"
          href="/"
          onClick={(event: React.MouseEvent<HTMLAnchorElement, MouseEvent>) => {
            handleClick(event, '/')
          }}
        >
          Home
        </Link>
        {hierarchy()}
      </Breadcrumbs>
    </div>
  )
}

export default Bread
