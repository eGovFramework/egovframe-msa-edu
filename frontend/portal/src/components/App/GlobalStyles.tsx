import { ASSET_PATH } from '@constants/env'
import React from 'react'

export interface IGlobalStyleProps {
  children: React.ReactNode
}

const GlobalStyles = ({ children }: IGlobalStyleProps) => {
  return (
    <div>
      {children}
      <style jsx global>
        {`
          @import '${ASSET_PATH}/layout.css';
        `}
      </style>
    </div>
  )
}

export default GlobalStyles
