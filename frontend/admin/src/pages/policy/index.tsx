import { GridButtons } from '@components/Buttons'
import Search from '@components/Search'
import CustomDataGrid from '@components/Table/CustomDataGrid'
import { GRID_PAGE_SIZE } from '@constants'
import usePage from '@hooks/usePage'
import useSearchTypes from '@hooks/useSearchType'
// 내부 컴포넌트 및 custom hook, etc...
import { convertStringToDateFormat } from '@libs/date'
// material-ui deps
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import {
  GridCellParams,
  GridColDef,
  GridValueFormatterParams,
  GridValueGetterParams,
} from '@material-ui/data-grid'
//api
import { policyService } from '@service'
import { conditionAtom, errorStateSelector } from '@stores'
import { Page, rownum } from '@utils'
import { AxiosError } from 'axios'
import { GetServerSideProps, NextPage } from 'next'
import { TFunction, useTranslation } from 'next-i18next'
import { useRouter } from 'next/router'
import React, { useCallback, useMemo } from 'react'
// 상태관리 recoil
import { useRecoilValue, useSetRecoilState } from 'recoil'

// material-ui style
const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
      '& .MuiOutlinedInput-input': {
        padding: theme.spacing(1),
      },
    },
  }),
)

//그리드 컬럼 정의
type ColumnsType = (
  data: Page,
  typeList: IPolicyType[],
  deletePolicy: (id: string) => void,
  updatePolicy: (id: string) => void,
  toggleIsUse: (event: React.ChangeEvent<HTMLInputElement>, id: string) => void,
  t?: TFunction,
) => GridColDef[]

const getColumns: ColumnsType = (
  data,
  typeList,
  deletePolicy,
  updatePolicy,
  toggleIsUse,
  t,
) => {
  return [
    {
      field: 'rownum',
      headerName: t('common.no'),
      headerAlign: 'center',
      align: 'center',
      sortable: false,
      valueGetter: (params: GridValueGetterParams) =>
        rownum(data, params.api.getRowIndex(params.id), 'desc'),
    },
    {
      field: 'type',
      headerName: t('common.type'),
      headerAlign: 'center',
      align: 'center',
      width: 150,
      sortable: false,
      valueGetter: (params: GridValueGetterParams) => {
        const type = typeList?.find(item => item.codeId === params.value)
        return type?.codeName || ''
      },
    },
    {
      field: 'title',
      headerName: t('policy.title'),
      headerAlign: 'center',
      width: 200,
      sortable: false,
    },
    {
      field: 'isUse',
      headerName: t('common.use_at'),
      headerAlign: 'center',
      align: 'center',
      width: 120,
      sortable: false,
      renderCell: (params: GridCellParams) => (
        <Switch
          checked={Boolean(params.value)}
          onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
            toggleIsUse(event, params.row.id)
          }
        />
      ),
    },
    {
      field: 'regDate',
      headerName: t('common.created_datetime'),
      headerAlign: 'center',
      align: 'center',
      width: 120,
      sortable: false,
      valueFormatter: (params: GridValueFormatterParams) => {
        return convertStringToDateFormat(
          params.value as string,
          'yyyy-MM-dd HH:mm:ss',
        )
      },
    },
    {
      field: 'id',
      headerName: t('common.manage'),
      headerAlign: 'center',
      align: 'center',
      width: 200,
      sortable: false,
      renderCell: (params: GridCellParams) => (
        <GridButtons
          id={params.value as string}
          handleDelete={deletePolicy}
          handleUpdate={updatePolicy}
        />
      ),
    },
  ]
}

const conditionKey = 'policy'

export interface IPolicyType {
  codeId: string
  codeName: string
  sortSeq: number
}

export interface IPolicyProps {
  typeList: IPolicyType[]
}

// 실제 render되는 컴포넌트
const Policy: NextPage<IPolicyProps> = ({ typeList }) => {
  // props 및 전역변수
  // const { id } = props
  const classes = useStyles()
  const route = useRouter()

  const { t } = useTranslation()
  /**
   * 상태관리 필요한 훅
   */
  //조회조건 상태관리
  const keywordState = useRecoilValue(conditionAtom(conditionKey))
  const setErrorState = useSetRecoilState(errorStateSelector)

  //현 페이지내 필요한 hook
  const { page, setPageValue } = usePage(conditionKey)

  //목록 데이터 조회 및 관리
  const { data, mutate } = policyService.search({
    keywordType: keywordState?.keywordType || 'title',
    keyword: keywordState?.keyword || '',
    size: GRID_PAGE_SIZE,
    page,
  })

  //조회조건 select items
  const searchTypes = useSearchTypes([
    {
      key: 'title',
      label: t('policy.title'),
    },
    {
      key: 'contents',
      label: t('comment.comment_content'),
    },
  ])

  /**
   * 비지니스 로직
   */

  //에러 callback
  const errorCallback = useCallback((error: AxiosError) => {
    setErrorState({
      error,
    })
  }, [])

  //삭제
  const deletePolicy = useCallback((id: string) => {
    policyService.delete({
      callback: mutate,
      errorCallback,
      id,
    })
  }, [])

  //수정 시 상세 화면 이동
  const updatePolicy = useCallback((id: string) => {
    route.push(`/policy/${id}`)
  }, [])

  //사용여부 toggle 시 바로 update
  const toggleIsUse = useCallback(
    async (event: React.ChangeEvent<HTMLInputElement>, id: string) => {
      policyService.updateUse({
        callback: mutate,
        errorCallback,
        id,
        isUse: event.target.checked,
      })
    },
    [page],
  )

  // 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const columns = useMemo(() => {
    return getColumns(
      data,
      typeList,
      deletePolicy,
      updatePolicy,
      toggleIsUse,
      t,
    )
  }, [data])

  //목록 조회
  const handleSearch = () => {
    if (page === 0) {
      mutate()
    } else {
      setPageValue(0)
    }
  }

  //datagrid page change event
  const handlePageChange = (page: number, details?: any) => {
    setPageValue(page)
  }

  return (
    <div className={classes.root}>
      <Search
        keywordTypeItems={searchTypes}
        handleSearch={handleSearch}
        handleRegister={() => {
          route.push('policy/-1')
        }}
        conditionKey={conditionKey}
      />

      <CustomDataGrid
        classes={classes}
        rows={data?.content}
        columns={columns}
        rowCount={data?.totalElements}
        paginationMode="server"
        pageSize={GRID_PAGE_SIZE}
        page={page}
        onPageChange={handlePageChange}
      />
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async context => {
  let typeList: IPolicyType[] = []
  try {
    const result = await policyService.getTypeList()
    if (result) {
      typeList = result.data
    }
  } catch (error) {
    console.error(`policy list getServerSideProps error ${error.message}`)
  }

  return {
    props: {
      typeList,
    },
  }
}

export default Policy
