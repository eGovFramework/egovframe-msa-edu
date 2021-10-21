import { FAQBaordList, NormalBoardList } from '@components/BoardList'
import { BottomButtons, IButtons } from '@components/Buttons'
import usePage from '@hooks/usePage'
import { boardService, IBoard } from '@service'
import { conditionAtom, userAtom } from '@stores'
import { GetServerSideProps } from 'next'
import { useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { useMemo, useState } from 'react'
import { useRecoilValue } from 'recoil'

interface BoardProps {
  board: IBoard
}

const Board = ({ board }: BoardProps) => {
  const router = useRouter()
  const { query } = router
  const { t } = useTranslation()

  const user = useRecoilValue(userAtom)

  const conditionKey = useMemo(() => {
    if (query) {
      return `board-${query.board}`
    }

    return undefined
  }, [query])

  // 조회조건 상태관리
  const keywordState = useRecoilValue(conditionAtom(conditionKey))

  const { page, setPageValue } = usePage(conditionKey)
  const [pageSize, setPageSize] = useState<number>(board?.postDisplayCount)

  const { data, mutate } = boardService.search(
    parseInt(query?.board as string, 10),
    {
      keywordType: keywordState?.keywordType || 'postsTitle',
      keyword: keywordState?.keyword || '',
      size: pageSize,
      page,
    },
  )

  const handlePageSize = (size: number) => {
    setPageSize(size)
  }

  const handleSearch = () => {
    if (page === 0) {
      mutate()
    } else {
      setPageValue(0)
    }
  }

  const handleButtons = useMemo(
    (): IButtons[] => [
      {
        id: 'regist',
        title: t('label.button.reg'),
        href: `${router.asPath}/edit/-1`,
        className: 'blue',
      },
    ],
    [t, router.asPath],
  )

  // datagrid page change event
  const handlePageChange = (_page: number, details?: any) => {
    setPageValue(_page)
  }

  const handleChangePage = (
    event: React.MouseEvent<HTMLButtonElement> | null,
    _page: number,
  ) => {
    setPageValue(_page)
  }

  return (
    <div className={query?.skin === 'faq' ? 'qna_list' : 'table_list01'}>
      {query?.skin === 'normal' && (
        <NormalBoardList
          data={data}
          pageSize={pageSize}
          handlePageSize={handlePageSize}
          handleSearch={handleSearch}
          conditionKey={conditionKey}
          page={page}
          handlePageChange={handlePageChange}
        />
      )}
      {query?.skin === 'faq' && (
        <FAQBaordList
          data={data}
          pageSize={pageSize}
          page={page}
          handleChangePage={handleChangePage}
        />
      )}
      {query?.skin === 'qna' && (
        /* <QnABaordList
          data={data}
          pageSize={pageSize}
          page={page}
          handleChangePage={handleChangePage}
        /> */
        <NormalBoardList
          data={data}
          pageSize={pageSize}
          handlePageSize={handlePageSize}
          handleSearch={handleSearch}
          conditionKey={conditionKey}
          page={page}
          handlePageChange={handlePageChange}
        />
      )}
      {user && board.userWriteAt === true && (
        <BottomButtons handleButtons={handleButtons} />
      )}
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async context => {
  const boardNo = Number(context.query.board)

  let board = {}

  try {
    const result = await boardService.getBoardById(boardNo)
    if (result) {
      board = result.data
    }
  } catch (error) {
    console.error(`board query error : ${error.message}`)
  }

  return {
    props: {
      board,
    },
  }
}

export default Board
