import Loader from '@components/Loader'
import { GetServerSideProps } from 'next'
import React from 'react'

/**
 * 통계 페이지로 redirect
 */
const Home = () => {
  return (
    <>
      <Loader />
    </>
  )
}

export const getServerSideProps: GetServerSideProps = async ({ query }) => {
  return {
    redirect: {
      permanent: true,
      destination: '/statistics',
    },
  }
}

export default Home
