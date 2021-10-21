import { createTheme, colors } from '@material-ui/core'
import typography from './typography'
import shadows from './shadows'

/**
 * @TODO
 * custom theme에 필요한 것들 지정
 */

// Create a theme instance.
const darkTheme = createTheme({
  palette: {
    background: {
      default: '#192038',
      paper: colors.common.black,
    },
    primary: {
      contrastText: '#192038',
      main: '#5664d2',
    },
    text: {
      primary: '#f2f6ff',
      secondary: '#6b778c',
    },
  },
  shadows,
  typography,
})

export default darkTheme
