import React from 'react'
import { NaverLoginButton } from '@components/Buttons'

const LoginNaver = () => {
  /* const router = useRouter()

  useEffect(() => {
    if (router.asPath) {
      const token = router.asPath.split('=')[1].split('&')[0]
      window.opener.naver.successCallback(token)
      window.close()
    }
  }, [router.asPath])

  return <></> */

  return <div style={{ display: 'none' }}><NaverLoginButton handleClick={() => {}} /></div>
}

export default LoginNaver
