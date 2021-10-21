import Editor from '@components/Editor'
import { contentService, IContent } from '@service'
import { GetServerSideProps } from 'next'
import React, { useEffect, useState } from 'react'

export interface IContentItemsProps {
  content: IContent | null
}

const Content = ({ content }: IContentItemsProps) => {
  const [contents, setContents] = useState(null)

  useEffect(() => {
    if (content) {
      setContents(content.contentValue)
    }
  }, [content])
  return (
    <Editor contents={contents} setContents={setContents} readonly={true} />
  )
  // return (
  //   <article className="intro">
  //     <div dangerouslySetInnerHTML={{ __html: content.contentValue }} />
  //   </article>
  // )
}

export const getServerSideProps: GetServerSideProps = async context => {
  const { query } = context
  const contentNo = Number(query.id)

  let content = {}

  try {
    const result = await contentService.get(contentNo)
    if (result) {
      content = (await result.data) as IContent
    }
  } catch (error) {
    console.error(`content item query error ${error.message}`)
    if (error.response?.data?.code === 'E003') {
      return {
        notFound: true,
      }
    }
  }

  return {
    props: {
      content,
    },
  }
}

export default Content
