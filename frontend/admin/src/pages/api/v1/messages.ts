import { ACCESS_TOKEN, CLAIM_NAME, SERVER_API_URL } from '@constants/env'
import axios from 'axios'
import Cookies from 'cookies'
import fs from 'fs'
import { NextApiRequest, NextApiResponse } from 'next'

export const config = {
  api: {
    bodyParser: false,
  },
}

const MESSAGE_URL = `${SERVER_API_URL}/portal-service/api/v1/messages/`
const locales = ['ko', 'en']
const FILE_PATH = `public/locales/`

/**
 * messages reload
 */
export default async (req: NextApiRequest, res: NextApiResponse) => {
  const cookies = new Cookies(req, res)
  const authToken = cookies.get(ACCESS_TOKEN)

  // server 에 cookie 전달하지 않음
  req.headers.cookie = ''
  //  header에 authentication 추가
  if (authToken) {
    req.headers[CLAIM_NAME] = authToken
  }

  let noResultLocales: string[] = []

  for (const locale of locales) {
    try {
      const result = await axios.get(`${MESSAGE_URL}${locale}`, {
        headers: {
          ...req.headers,
        },
      })

      if (result) {
        const jsonstring = JSON.stringify(result.data)

        await fs.writeFileSync(`${FILE_PATH}${locale}/common.json`, jsonstring)
      } else {
        noResultLocales.push(locale)
      }
    } catch (error) {
      console.error('catch error', error.message)
      noResultLocales.push(locale)
    }
  }

  if (noResultLocales.length > 0) {
    res
      .status(500)
      .json({ message: `Not Found Messages for ${noResultLocales.join(', ')}` })
  } else {
    res.status(200).json({ message: 'Success!!' })
  }
}
