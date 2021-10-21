import ValidationAlert from '@components/ValidationAlert'
import React from 'react'
import { FieldError } from 'react-hook-form'

interface DLWrapperProps {
  title: string
  error?: FieldError
  className?: string
  required?: boolean
  children: React.ReactNode
}

const DLWrapper = (props: DLWrapperProps) => {
  const { title, error, className, required, children } = props

  return (
    <dl>
      <dt className={required ? 'import' : ''}>{title}</dt>
      <dd className={className}>
        {children}
        {error && <ValidationAlert fieldError={error} label={title} />}
      </dd>
    </dl>
  )
}

export { DLWrapper }
