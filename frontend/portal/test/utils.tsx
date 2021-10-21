import React from 'react'
import { render, RenderOptions } from '@testing-library/react'

//Add in any providers here if necessary : (ReduxProvider, ThemeProvider, etc...)

const Provider = ({ children }: { children: React.ReactElement }) => {
  return children
}

const customRender = (
  ui: React.ReactElement,
  options?: Omit<RenderOptions, 'queries'>,
) => render(ui, { wrapper: Provider as React.ComponentType, ...options })

// re-export everything
export * from '@testing-library/react'

// override render method
export { customRender as render }
