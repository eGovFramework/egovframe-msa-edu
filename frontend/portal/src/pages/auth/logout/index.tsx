import { AUTH_USER_ID, CLAIM_NAME, REFRESH_TOKEN } from '@constants/env'
import axios from 'axios'
import { GetServerSideProps } from 'next'
import { useEffect } from 'react'

function Logout() {
  useEffect(() => {
    axios.defaults.headers.common[CLAIM_NAME] = ''
    axios.defaults.headers.common[AUTH_USER_ID] = ''
  }, [])

  return (
    <div>
      <a href="/auth/logout">Logout</a>
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async ({
  req,
  res,
  query,
}) => {
  if (!process.browser) {
    const Cookies = require('cookies')
    const cookies = new Cookies(req, res)

    // Delete the cookie by not setting a value
    cookies.set(REFRESH_TOKEN)
    axios.defaults.headers.common[CLAIM_NAME] = ''
    axios.defaults.headers.common[AUTH_USER_ID] = ''

    try {
      const { redirect } = req['query']

      res.writeHead(307, {
        Location: typeof redirect !== 'undefined' ? redirect : '/',
      })
      res.end()
    } catch (error) {
      res.writeHead(307, { Location: '/' })
      res.end()
    }
  }

  return {
    props: {},
  }
}

export default Logout
