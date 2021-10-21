import ActiveLink from '@components/ActiveLink'
import React from 'react'

export interface IButtons {
  id: ValueType
  title: string
  href: string
  className?: string
  handleClick?: () => void
}

interface BottomButtonsProps {
  handleButtons: IButtons[]
}

const BottomButtons = (props: BottomButtonsProps) => {
  const { handleButtons } = props
  return (
    <div className="btn_center">
      {handleButtons &&
        handleButtons.map(item => (
          <ActiveLink
            href={item.href}
            children={item.title}
            key={`bottom-button-${item.id}`}
            className={item.className || ''}
            handleActiveLinkClick={item.handleClick}
          />
        ))}
    </div>
  )
}

export { BottomButtons }
