import Loader from '@components/Loader'
import React from 'react'
import GlobalError from './GlobalError'
import SSRSafeSuspense from './SSRSafeSuspense'

export interface IWrapperProps {
  children: React.ReactNode
}

const Wrapper = ({ children }: IWrapperProps) => {
  return (
    <>
      <SSRSafeSuspense fallback={<Loader />}>{children}</SSRSafeSuspense>
      <GlobalError />
    </>
  )
}

export default Wrapper
