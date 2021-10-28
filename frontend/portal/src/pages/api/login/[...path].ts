import { CUSTOM_HEADER_SITE_ID_KEY, DEFAULT_ERROR_MESSAGE } from '@constants'
import {
  ACCESS_TOKEN,
  AUTH_USER_ID,
  CLAIM_NAME,
  REFRESH_TOKEN,
  SERVER_API_URL,
  SITE_ID,
} from '@constants/env'
import axios from 'axios'
import Cookies from 'cookies'
import { NextApiRequest, NextApiResponse } from 'next'
import url from 'url'

export default async (req: NextApiRequest, res: NextApiResponse) => {
  const pathname = url.pathToFileURL(req.url).pathname
  let isLogin = pathname === '/api/login/user-service/login'

  req.url = req.url.replace(/^\/api\/login/, '')

  if (pathname.indexOf('undefined') > -1) {
    res.status(500).json({ message: DEFAULT_ERROR_MESSAGE })
    res.end()
    return
  }

  let headers = {
    'Content-Type': 'application/json',
  }
  headers[CUSTOM_HEADER_SITE_ID_KEY] = SITE_ID

  //silent refresh
  if (pathname.indexOf('/refresh') > -1) {
    isLogin = true
    const cookies = new Cookies(req, res)
    headers[CLAIM_NAME] = cookies.get(REFRESH_TOKEN)

    if (!headers[CLAIM_NAME] || headers[CLAIM_NAME] === '') {
      console.warn(`can't refresh`)
      res.status(401).json({ message: 'Invalid Credentials 🥺' })
      return
    }
  }

  try {
    const result = await fetch(`${SERVER_API_URL}${req.url}`, {
      method: req.method,
      headers,
      body: req.body,
    })

    if (result) {
      const refreshToken = result.headers.get(REFRESH_TOKEN)
      const accessToken = result.headers.get(ACCESS_TOKEN)
      const userId = result.headers.get(AUTH_USER_ID)

      const cookies = new Cookies(req, res)

      cookies.set(REFRESH_TOKEN, refreshToken, {
        httpOnly: true,
        sameSite: 'lax', //CSRF protection
      })

      if (accessToken) {
        let payload = {}
        payload[ACCESS_TOKEN] = accessToken
        payload[AUTH_USER_ID] = userId
        axios.defaults.headers.common[CLAIM_NAME] = accessToken
        axios.defaults.headers.common[AUTH_USER_ID] = userId

        res.status(200).json(payload)
      } else {
        res.status(result.status).json({ message: 'Invalid Credentials 🥺' })
      }
    } else {
      res.status(401).json({ message: 'Invalid Credentials 🥺' })
    }
  } catch (error) {
    console.error(error)
    res.status(500).json(error)
  }
}
