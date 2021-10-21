import { useRouter } from 'next/router'
import React from 'react'

interface ActiveLinkProps
  extends React.DetailedHTMLProps<
    React.AnchorHTMLAttributes<HTMLAnchorElement>,
    HTMLAnchorElement
  > {
  children: React.ReactNode
  handleActiveLinkClick?: () => void
}

const ActiveLink = (props: ActiveLinkProps) => {
  const { children, handleActiveLinkClick, href, ...rest } = props
  const router = useRouter()

  const handleClick = (event: React.MouseEvent<HTMLAnchorElement>) => {
    event.preventDefault()

    if (handleActiveLinkClick) {
      handleActiveLinkClick()
      return
    }

    if (href === 'prev') {
      router.back()
      return
    }
    router.push(href)
  }

  return (
    <a href={href} onClick={handleClick} {...rest}>
      {children}
    </a>
  )
}

export default ActiveLink
