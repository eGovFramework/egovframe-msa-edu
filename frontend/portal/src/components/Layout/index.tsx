import React from 'react'
import Body from './Body'
import Footer from './Footer'
import Header from './Header'
import NoLeftBody from './NoLeftBody'

export interface LayoutProps {
  children: React.ReactNode
  main?: boolean
  isLeft?: boolean
}

const Layout = (props: LayoutProps) => {
  return (
    <div id="wrap">
      <Header />
      {props.isLeft ? <Body {...props} /> : <NoLeftBody {...props} />}
      <Footer />
    </div>
  )
}

export default Layout
