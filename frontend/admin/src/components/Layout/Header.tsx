import React, { useCallback } from 'react'
import clsx from 'clsx'
import AppBar from '@material-ui/core/AppBar'
import Toolbar from '@material-ui/core/Toolbar'
import IconButton from '@material-ui/core/IconButton'
import MenuIcon from '@material-ui/icons/Menu'
import Typography from '@material-ui/core/Typography'
import { Theme, makeStyles, createStyles } from '@material-ui/core/styles'

import { DEFAULT_APP_NAME, DRAWER_WIDTH } from '@constants'
import Profile from './Profile'
import useUser from '@hooks/useUser'
import { useRecoilValue } from 'recoil'
import { currentMenuStateAtom } from '@stores'
import { useTranslation } from 'react-i18next'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
    },
    menuButton: {
      marginRight: theme.spacing(2),
    },
    title: {
      flexGrow: 1,
    },
    appBar: {
      zIndex: theme.zIndex.drawer + 1,
      transition: theme.transitions.create(['width', 'margin'], {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
      }),
    },
    appBarShift: {
      marginLeft: DRAWER_WIDTH,
      width: `calc(100% - ${DRAWER_WIDTH}px)`,
      transition: theme.transitions.create(['width', 'margin'], {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.enteringScreen,
      }),
    },
    hide: {
      display: 'none',
    },
    toolbar: {
      paddingRight: 24, // keep right padding when drawer closed
    },
  }),
)

export interface IHeaderProps {
  open: boolean
  onClick: () => void
}

const Header: React.FC<IHeaderProps> = ({ open, onClick }) => {
  const classes = useStyles()
  const { user } = useUser()
  const currentMenu = useRecoilValue(currentMenuStateAtom)

  const { i18n } = useTranslation()

  const getTitle = useCallback(() => {
    if (currentMenu) {
      return i18n.language === 'ko'
        ? currentMenu?.korName
        : currentMenu?.engName
    }

    return DEFAULT_APP_NAME
  }, [i18n, currentMenu])

  return (
    <AppBar
      position="fixed"
      className={clsx(classes.appBar, {
        [classes.appBarShift]: open,
      })}
    >
      <Toolbar className={classes.toolbar}>
        <IconButton
          color="inherit"
          aria-label="open drawer"
          onClick={onClick}
          edge="start"
          className={clsx(classes.menuButton, {
            [classes.hide]: open,
          })}
        >
          <MenuIcon />
        </IconButton>
        <Typography variant="h4" noWrap className={classes.title}>
          {getTitle()}
        </Typography>

        {user && <Profile id={user.userId} email={user.email} />}
      </Toolbar>
    </AppBar>
  )
}

export default Header
