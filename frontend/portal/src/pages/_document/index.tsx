import React from 'react'
import { ServerStyleSheets } from '@material-ui/core/styles'
import Document, {
  DocumentContext,
  Head,
  Html,
  Main,
  NextScript,
} from 'next/document'

export default class MyDocument extends Document {
  loadWindowProperty = locale => (
    <script
      dangerouslySetInnerHTML={{ __html: `window.__localeId__= "${locale}"` }}
    ></script>
  )
  render() {
    const { loadWindowProperty } = this
    const { locale } = this.props
    return (
      <Html lang={locale}>
        <Head />
        <body>
          {this.loadWindowProperty(locale)}
          <Main />
          <NextScript />
        </body>
      </Html>
    )
  }
}

MyDocument.getInitialProps = async (ctx: DocumentContext) => {
  const sheets = new ServerStyleSheets()
  const originalRenderPage = ctx.renderPage

  ctx.renderPage = () =>
    originalRenderPage({
      enhanceApp: App => props => sheets.collect(<App {...props} />),
    })

  const initialProps = await Document.getInitialProps(ctx)

  return {
    ...initialProps,
    // Styles fragment is rendered after the app and page rendering finish.
    styles: [
      ...React.Children.toArray(initialProps.styles),
      sheets.getStyleElement(),
    ],
  }
}
