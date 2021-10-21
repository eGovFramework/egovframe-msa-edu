import { REFRESH_TOKEN } from '@constants/env'
import Cookies from 'cookies'
import { NextApiRequest, NextApiResponse } from 'next'

/**
 * refresh token 만료 시 쿠키 삭제
 */
export default (req: NextApiRequest, res: NextApiResponse) => {
  const cookies = new Cookies(req, res)

  // Delete the cookie by not setting a value
  cookies.set(REFRESH_TOKEN)

  res.status(200).json({ message: 'success' })
}
