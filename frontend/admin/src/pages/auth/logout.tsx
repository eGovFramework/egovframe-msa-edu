import { ACCESS_TOKEN, AUTH_USER_ID, REFRESH_TOKEN } from '@constants/env'
import axios from 'axios'

function Logout() {
  axios.defaults.headers.common[ACCESS_TOKEN] = ''
  axios.defaults.headers.common[AUTH_USER_ID] = ''
  return (
    <div>
      <a href="/auth/logout">Logout</a>
    </div>
  )
}

Logout.getInitialProps = ({ req, res }) => {
  if (!process.browser) {
    const Cookies = require('cookies')
    const cookies = new Cookies(req, res)

    // Delete the cookie by not setting a value
    cookies.set(REFRESH_TOKEN)
    cookies.set(ACCESS_TOKEN)

    res.writeHead(307, { Location: '/' })
    res.end()
  } else {
    return {}
  }
}

export default Logout
