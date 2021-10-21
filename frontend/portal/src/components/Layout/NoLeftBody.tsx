import React from 'react'
import { LayoutProps } from '.'

interface NoLeftBodyProps extends LayoutProps {}

const NoLeftBody = (props: NoLeftBodyProps) => {
  const { children, main } = props

  return (
    <>
      {main ? (
        <div id="main">{children}</div>
      ) : (
        <div id="container">
          <div>{children}</div>
        </div>
      )}
    </>
  )
}

export default NoLeftBody
