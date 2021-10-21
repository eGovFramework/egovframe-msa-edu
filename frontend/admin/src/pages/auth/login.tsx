import LoginForm, { loginFormType } from '@components/Auth/LoginForm'
import Loader from '@components/Loader'
import { DEFAULT_ERROR_MESSAGE } from '@constants'
import useUser from '@hooks/useUser'
import Router from 'next/router'
import React, { useEffect, useState } from 'react'
import { loginSerivce } from 'src/service/Login'

const Login = () => {
  const { isLogin, loggedOut, mutate } = useUser()
  const [loginError, setLoginError] = useState<string | null>(null)

  useEffect(() => {
    if (isLogin && !loggedOut) {
      Router.replace('/')
    }
  }, [isLogin, loggedOut])

  if (isLogin) {
    return <Loader />
  }

  const onLoginSubmit = async (form: loginFormType) => {
    try {
      const result = await loginSerivce.login(form)
      if (result === 'success') {
        mutate()
      } else {
        setLoginError(result)
      }
    } catch (error) {
      console.error('login error ', error)
      setLoginError(error.response?.data.message || DEFAULT_ERROR_MESSAGE)
    }
  }

  return <LoginForm handleLogin={onLoginSubmit} errorMessage={loginError} />
}

export default Login
