import Paper from '@material-ui/core/Paper'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Tabs, { TabsProps } from '@material-ui/core/Tabs'
import React, { useState } from 'react'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
      background: theme.palette.background.default,
    },
  }),
)

interface HorizontalTabsProps extends TabsProps {
  tabs: React.ReactNode
  init: string | number
  handleTab: (value: string | number) => void
}

const HorizontalTabs = (props: HorizontalTabsProps) => {
  const { tabs, init, handleTab, ...rest } = props
  const classes = useStyles()
  const [value, setValue] = useState<string | number>(init)

  const handleChange = (event: React.ChangeEvent<{}>, newValue: number) => {
    handleTab(newValue)
    setValue(newValue)
  }

  return (
    <Paper className={classes.root}>
      <Tabs
        value={value}
        onChange={handleChange}
        indicatorColor="primary"
        textColor="primary"
        {...rest}
      >
        {tabs}
      </Tabs>
    </Paper>
  )
}

export { HorizontalTabs }
