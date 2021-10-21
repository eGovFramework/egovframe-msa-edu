import useMounted from '@hooks/useMounted'
import React, { Suspense, SuspenseProps } from 'react'

const SSRSafeSuspense = (props: SuspenseProps) => {
  const isMounted = useMounted()

  if (isMounted) {
    return <Suspense {...props} />
  }

  return <>{props.fallback}</>
}

export default SSRSafeSuspense
