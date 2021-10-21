import React from 'react'
import { Typography } from '@material-ui/core'
import Link from '@material-ui/core/Link'
import { getCurrentDate } from '@libs/date'

const Copyright = () => {
  return (
    <Typography variant="body2" color="textSecondary">
      {'Copyright © '}
      <Link color="inherit" href="https://material-ui.com/">
        Your Website
      </Link>{' '}
      {getCurrentDate().getFullYear()}
      {'.'}
    </Typography>
  )
}

export default Copyright
