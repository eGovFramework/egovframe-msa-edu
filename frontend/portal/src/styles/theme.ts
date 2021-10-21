import { createTheme } from '@material-ui/core/styles'

// declare module '@material-ui/core/styles/createBreakpoints' {
//   interface BreakpointOverrides {
//     xs: false // removes the `xs` breakpoint
//     sm: false
//     md: false
//     lg: false
//     xl: false
//     tablet: true // adds the `tablet` breakpoint
//     laptop: true
//     desktop: true
//   }
// }

const theme = createTheme({
  breakpoints: {
    values: {
      xs: 0,
      sm: 640,
      md: 1024,
      lg: 1280,
      xl: 1920,
    },
  },
})

export default theme
