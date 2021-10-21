import React from 'react'
import CustomErrorPage from '@components/Errors'

const Error404: React.FC = () => {
  return <CustomErrorPage statusCode={404} />
}

export default Error404
