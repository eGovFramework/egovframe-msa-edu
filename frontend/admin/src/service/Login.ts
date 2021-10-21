import { loginFormType } from '@components/Auth/LoginForm'
import { ACCESS_TOKEN, AUTH_USER_ID, CLAIM_NAME } from '@constants/env'
import axios from 'axios'

const JWT_EXPIRED_TIME = 1800000
const LOGIN_URL = `/user-service/login`
const CLIENT_REFRESH_URL = `/client/refresh`

const onSuccessLogin = (result: any) => {
  axios.defaults.headers.common[CLAIM_NAME] = result[ACCESS_TOKEN]
  axios.defaults.headers.common[AUTH_USER_ID] = result[AUTH_USER_ID]
  // access-token 만료 1분 전에 로그인 연장
  setTimeout(loginSerivce.silentRefresh, JWT_EXPIRED_TIME - 60000)
}

export const loginSerivce = {
  login: (data: loginFormType) => {
    return new Promise<string>(async (resolve, reject) => {
      try {
        const result = await fetch(`/api/proxy${LOGIN_URL}`, {
          method: 'POST',
          body: JSON.stringify(data),
        })

        if (result.ok === true) {
          onSuccessLogin(await result.json())
          resolve('success')
        } else {
          reject('noAuth')
        }
      } catch (error) {
        reject(error)
      }
    })
  },
  silentRefresh: async () => {
    try {
      // const result = await axios.put(CLIENT_REFRESH_URL)
      const result = await fetch(`/api/proxy${CLIENT_REFRESH_URL}`, {
        method: 'PUT',
      })
      if (result) {
        onSuccessLogin(await result.json())
      }
    } catch (error) {
      console.warn('refresh token 만료로 인한 로그아웃!!!!')
      fetch('/api/v1/token')
        .then(res => {
          console.info('fetch', res)
        })
        .catch(error => {
          console.info('fetch error', error)
        })
    }
  },
}
