import React from 'react'
import { Container, Grid } from '@material-ui/core'
import { makeStyles, createStyles, Theme } from '@material-ui/core/styles'
import SideBar from './SideBar'
import Header from './Header'
import Footer from './Footer'
import { PageProps } from '@pages/_app'
import Bread from './Bread'
import { ADMIN_LOGO_PATH, ADMIN_LOGO_TEXT } from '@constants'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
      backgroundColor: theme.palette.background.paper,
    },
    content: {
      flexGrow: 1,
      display: 'flex',
      flexDirection: 'column',
      minHeight: '100vh',
    },
    container: {
      paddingTop: theme.spacing(2),
      paddingBottom: theme.spacing(2),
      marginBottom: theme.spacing(1),
    },
    toolbar: {
      paddingRight: 24, // keep right padding when drawer closed
    },
    appBarSpacer: theme.mixins.toolbar,
    authContent: {
      padding: '2.5rem',
    },
  }),
)

interface ILayoutProps extends PageProps {
  children: React.ReactNode
  className?: string
}

const Layout: React.FC<ILayoutProps> = props => {
  const { children, className } = props
  const classes = useStyles()
  const [open, setOpen] = React.useState(false)

  const handleDrawerOpen = () => {
    setOpen(true)
  }

  const handleDrawerClose = () => {
    setOpen(false)
  }

  return (
    <div className={`${classes.root} ${className}`}>
      {/* <CssBaseline /> */}
      <Header open={open} onClick={handleDrawerOpen} />

      <SideBar
        open={open}
        onClick={handleDrawerClose}
        logoText={ADMIN_LOGO_TEXT}
        logo={ADMIN_LOGO_PATH}
      />

      <main className={classes.content}>
        <div className={classes.appBarSpacer} />
        <Container maxWidth="lg" className={classes.container}>
          <Bread />
          {children}
          <Grid container spacing={3}></Grid>
        </Container>
        <Footer />
      </main>
    </div>
  )
}

export { Layout }
