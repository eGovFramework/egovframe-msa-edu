import React, { useCallback } from 'react'
import { useRouter } from 'next/router'
import Typography from '@material-ui/core/Typography'
import Breadcrumbs from '@material-ui/core/Breadcrumbs'
import Link from '@material-ui/core/Link'
import { Theme, makeStyles } from '@material-ui/core/styles'
import { currentMenuStateAtom, flatMenusSelect } from '@stores'
import { useRecoilValue } from 'recoil'
import { useTranslation } from 'react-i18next'

const useStyles = makeStyles((theme: Theme) => ({
  root: {
    // marginBottom: theme.spacing(1),
  },
}))

const Bread: React.FC = () => {
  const classes = useStyles()
  const router = useRouter()
  const flatMenus = useRecoilValue(flatMenusSelect)
  const current = useRecoilValue(currentMenuStateAtom)
  const { i18n } = useTranslation()

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
    const arr = flatMenus.slice(
      0,
      flatMenus.findIndex(item => item.id === current.id) + 1,
    )

    trees.push(current)
    arr.reverse().some(item => {
      if (item.level < current.level) {
        trees.push(item)
      }

      if (item.level === 1) {
        return true
      }
    })

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
