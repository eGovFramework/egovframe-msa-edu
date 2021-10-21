import React from 'react'
import { NextPageContext } from 'next'
import CustomErrorPage from '@components/Errors'

const Error = ({ statusCode }) => {
  return <CustomErrorPage statusCode={statusCode} />
}

Error.getInitialProps = ({ res, err }: NextPageContext) => {
  const statusCode = res ? res.statusCode : err ? err.statusCode : 404

  return { statusCode }
}

export default Error
