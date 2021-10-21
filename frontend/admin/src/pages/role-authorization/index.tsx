import { CustomButtons, IButtonProps } from '@components/Buttons'
import CustomAlert from '@components/CustomAlert'
import Search, { IKeywordType } from '@components/Search'
import CustomDataGrid from '@components/Table/CustomDataGrid'
import { GRID_PAGE_SIZE, GRID_ROW_HEIGHT } from '@constants'
import usePage from '@hooks/usePage'
// 내부 컴포넌트 및 custom hook, etc...
import { convertStringToDateFormat } from '@libs/date'
import { Button } from '@material-ui/core'
import Box from '@material-ui/core/Box'
// material-ui deps
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import Switch from '@material-ui/core/Switch'
import {
  DataGrid,
  GridCellParams,
  GridColDef,
  GridValueFormatterParams,
} from '@material-ui/data-grid'
// api
import {
  IRole,
  RoleAuthorizationSavePayload,
  roleAuthorizationService,
  roleService,
} from '@service'
import {
  conditionAtom,
  detailButtonsSnackAtom,
  errorStateSelector,
} from '@stores'
import { format, Page } from '@utils'
import { AxiosError } from 'axios'
import { GetServerSideProps } from 'next'
import { TFunction, useTranslation } from 'next-i18next'
import React, { useCallback, useMemo, useRef, useState } from 'react'
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

// 권한 그리드 컬럼 정의
type RoleColumnsType = (
  data: any[] | [],
  handleManageRole: (roleId: string) => void,
  t?: TFunction,
) => GridColDef[]

const getRoleColumns: RoleColumnsType = (data, handleManageRole, t) => [
  {
    field: 'roleId',
    headerName: t('role.role_id'),
    headerAlign: 'center',
    align: 'left',
    width: 200,
    sortable: false,
  },
  {
    field: 'roleName',
    headerName: t('role.role_name'),
    headerAlign: 'center',
    align: 'center',
    width: 200,
    sortable: false,
  },
  {
    field: 'roleContent',
    headerName: t('role.role_content'),
    headerAlign: 'center',
    flex: 1,
    sortable: false,
  },
  {
    field: 'createdDate',
    headerName: t('common.created_datetime'),
    headerAlign: 'center',
    align: 'center',
    width: 200,
    sortable: false,
    valueFormatter: (params: GridValueFormatterParams) =>
      convertStringToDateFormat(params.value as string, 'yyyy-MM-dd HH:mm:ss'),
  },
  {
    field: 'buttons',
    headerName: t('common.manage'),
    headerAlign: 'center',
    align: 'center',
    width: 150,
    sortable: false,
    renderCell: function renderCellRoleId(params: GridCellParams) {
      return (
        <div>
          <Box>
            <Button
              variant="outlined"
              color="primary"
              size="small"
              /* eslint-disable-next-line @typescript-eslint/no-empty-function */
              onClick={() => {
                handleManageRole(params.row.roleId)
              }}
            >
              {t('role.manage_authorization')}
            </Button>
          </Box>
        </div>
      )
    },
  },
]

// 인가 그리드 컬럼 정의
type AuthorizationColumnsType = (
  data: Page,
  toggleCreatedAt: (
    event: React.ChangeEvent<HTMLInputElement>,
    roleId: string,
    authorizationNo: number,
  ) => void,
  roleAuthorizationApiRef: React.MutableRefObject<any>,
  t?: TFunction,
) => GridColDef[]

const getAuthorizationColumns: AuthorizationColumnsType = (
  data,
  toggleCreatedAt,
  roleAuthorizationApiRef,
  t,
) => [
  {
    field: 'authorizationName',
    headerName: t('authorization.authorization_name'),
    headerAlign: 'center',
    align: 'left',
    width: 250,
    sortable: false,
  },
  {
    field: 'urlPatternValue',
    headerName: t('authorization.url_pattern_value'),
    headerAlign: 'center',
    align: 'left',
    flex: 1,
    sortable: false,
  },
  {
    field: 'httpMethodCode',
    headerName: t('authorization.http_method_code'),
    headerAlign: 'center',
    align: 'center',
    width: 140,
    sortable: false,
  },
  {
    field: 'sortSeq',
    headerName: t('common.sort_seq'),
    headerAlign: 'center',
    align: 'center',
    width: 110,
    sortable: false,
  },
  {
    field: 'createdAt',
    headerName: t('common.created_at'),
    headerAlign: 'center',
    align: 'center',
    width: 110,
    sortable: false,
    renderCell: function renderCellCreatedAt(params: GridCellParams) {
      // eslint-disable-next-line no-param-reassign
      roleAuthorizationApiRef.current = params.api // api
      return (
        <Switch
          checked={Boolean(params.value)}
          onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
            toggleCreatedAt(
              event,
              params.row.roleId as string,
              params.row.authorizationNo as number,
            )
          }
        />
      )
    },
  },
]

const conditionKey = 'authorization'

export interface IRoleAuthorizationProps {
  roles: IRole[]
  initRoleId: string
}

// 실제 render되는 컴포넌트
const RoleAuthorization = ({ roles, initRoleId }: IRoleAuthorizationProps) => {
  // props 및 전역변수
  // const { id } = props
  const classes = useStyles()
  const roleAuthorizationApiRef = useRef<any>(null)
  const { t } = useTranslation()

  // 버튼 component 상태 전이
  const setSuccessSnackBar = useSetRecoilState(detailButtonsSnackAtom)

  // 조회조건 select items
  const searchTypes: IKeywordType[] = [
    {
      key: 'authorizationName',
      label: t('authorization.authorization_name'),
    },
    {
      key: 'urlPatternValue',
      label: t('authorization.url_pattern_value'),
    },
    {
      key: 'httpMethodCode',
      label: t('authorization.http_method_code'),
    },
  ]

  /**
   * 상태관리 필요한 훅
   */
  // 조회조건 상태관리
  const keywordState = useRecoilValue(conditionAtom(conditionKey))
  const setErrorState = useSetRecoilState(errorStateSelector)

  // 현 페이지내 필요한 hook
  const [customAlert, setCustomAlert] = useState<any>({
    open: false,
    message: '',
  })
  const { page, setPageValue } = usePage(conditionKey)
  const [roleId, setRoleId] = useState<string>(initRoleId)

  /**
   * 비지니스 로직
   */

  // 권한 정보 초기화
  if (roles) {
    let role
    if (roleId) {
      role = roles.find(m => m.roleId === roleId)
    }
    if (role === undefined) {
      role = roles.find(m => m)
    }
    if (role !== undefined) {
      if (roleId !== role.roleId) setRoleId(role.roleId)
    }
  }

  // 인가 목록 조회
  const { data, mutate } = roleAuthorizationService.search(roleId, {
    keywordType: keywordState?.keywordType || 'authorizationName',
    keyword: keywordState?.keyword || '',
    size: GRID_PAGE_SIZE,
    page,
  })

  // 그리드 체크 해제
  const uncheckedGrid = useCallback(() => {
    const selectedRowKeys = roleAuthorizationApiRef.current
      ?.getSelectedRows()
      .keys()

    let cnt = 0
    while (cnt < data.numberOfElements) {
      const gridRowId = selectedRowKeys.next()
      if (gridRowId.done === true) break
      roleAuthorizationApiRef.current.selectRow(gridRowId.value, false, false)
      cnt += 1
    }
  }, [data?.numberOfElements])

  // 성공 callback
  const successCallback = useCallback(() => {
    setSuccessSnackBar('success')

    uncheckedGrid()

    mutate()
  }, [mutate, setSuccessSnackBar, uncheckedGrid])

  // 에러 callback
  const errorCallback = useCallback(
    (error: AxiosError) => {
      setSuccessSnackBar('none')

      setErrorState({
        error,
      })
    },
    [setErrorState, setSuccessSnackBar],
  )

  // 인가 toggle 시 save
  const toggleCreatedAt = useCallback(
    async (
      event: React.ChangeEvent<HTMLInputElement>,
      paramRoleId: string,
      paramAuthorizationNo: number,
    ) => {
      setSuccessSnackBar('loading')

      const selectedRow: RoleAuthorizationSavePayload = {
        roleId: paramRoleId,
        authorizationNo: paramAuthorizationNo,
      }

      if (event.target.checked) {
        await roleAuthorizationService.save({
          callback: successCallback,
          errorCallback,
          data: [selectedRow],
        })
      } else {
        await roleAuthorizationService.delete({
          callback: successCallback,
          errorCallback,
          data: [selectedRow],
        })
      }
    },
    [errorCallback, setSuccessSnackBar, successCallback],
  )

  // 권한매핑관리
  const handleManageRole = useCallback((_roleId: string) => {
    setRoleId(_roleId)
    setPageValue(0)
  }, [])

  // 권한 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const roleColumns = useMemo(
    () => getRoleColumns(roles, handleManageRole, t),
    [handleManageRole, roles, t],
  )

  // 인가 목록컬럼 재정의 > 컬럼에 비지니스 로직이 필요한 경우
  const authorizationColumns = useMemo(
    () =>
      getAuthorizationColumns(
        data,
        toggleCreatedAt,
        roleAuthorizationApiRef,
        t,
      ),
    [data, toggleCreatedAt, roleAuthorizationApiRef, t],
  )

  // 선택된 행 수 반환
  const getSelectedRowCount = (checked: boolean) => {
    let count = 0

    const selectedRows = roleAuthorizationApiRef.current.getSelectedRows()
    selectedRows.forEach(m => {
      if (m.createdAt === checked) {
        count += 1
      }
    })

    return count
  }

  // 선택된 행 반환
  const getSelectedRows = (checked: boolean) => {
    let list: RoleAuthorizationSavePayload[] = []

    const selectedRows = roleAuthorizationApiRef.current.getSelectedRows()
    selectedRows.forEach(m => {
      if (m.createdAt === checked) {
        const saved: RoleAuthorizationSavePayload = {
          roleId: m.roleId,
          authorizationNo: m.authorizationNo,
        }
        list.push(saved)
      }
    })

    return list
  }

  // 선택 저장
  const handleSave = useCallback(() => {
    setSuccessSnackBar('loading')

    const selectedRows = getSelectedRows(false)

    if (selectedRows.length === 0) {
      successCallback()
      return
    }

    roleAuthorizationService.save({
      callback: successCallback,
      errorCallback,
      data: selectedRows,
    })
  }, [setSuccessSnackBar, successCallback, errorCallback])

  // 선택 삭제
  const handleDelete = useCallback(() => {
    setSuccessSnackBar('loading')

    const selectedRows = getSelectedRows(true)

    if (selectedRows.length === 0) {
      successCallback()
      return
    }

    roleAuthorizationService.delete({
      callback: successCallback,
      errorCallback,
      data: selectedRows,
    })
  }, [setSuccessSnackBar, successCallback, errorCallback])

  // 선택 등록, 선택 삭제 버튼
  const saveButton: IButtonProps = {
    label: t('label.button.selection_registration'),
    variant: 'outlined',
    color: 'default',
    size: 'small',
    confirmMessage: t('msg.confirm.registration'),
    handleButton: handleSave,
    validate: () => {
      if (roleAuthorizationApiRef.current.getSelectedRows().size === 0) {
        setCustomAlert({
          open: true,
          message: format(t('valid.selection.format'), [
            `${t('label.button.reg')} ${t('common.target')}`,
          ]),
        })
        return false
      }
      const count = getSelectedRowCount(false) // 미등록만
      if (count === 0) {
        setCustomAlert({
          open: true,
          message: format(t('valid.selection.already_saved.format'), [
            t('authorization'),
          ]),
        })
        return false
      }
      return true
    },
    completeMessage: t('msg.success.save'),
  }
  const deleteButton: IButtonProps = {
    label: t('label.button.selection_delete'),
    variant: 'outlined',
    color: 'default',
    size: 'small',
    confirmMessage: t('msg.confirm.delete'),
    handleButton: handleDelete,
    validate: () => {
      if (roleAuthorizationApiRef.current.getSelectedRows().size === 0) {
        setCustomAlert({
          open: true,
          message: format(t('valid.selection.format'), [
            `${t('label.button.delete')} ${t('common.target')}`,
          ]),
        })
        return false
      }
      const count = getSelectedRowCount(true) // 등록만
      if (count === 0) {
        setCustomAlert({
          open: true,
          message: format(t('valid.selection.already_deleted.format'), [
            t('authorization'),
          ]),
        })
        return false
      }
      return true
    },
    completeMessage: t('msg.success.delete'),
  }

  // 목록 조회
  const handleSearch = () => {
    if (page === 0) {
      mutate()
    } else {
      setPageValue(0)
    }
  }

  // datagrid page change event
  const handlePageChange = (_page: number, details?: any) => {
    setPageValue(_page)
  }

  return (
    <div className={classes.root}>
      <DataGrid
        rows={roles || []}
        columns={roleColumns}
        rowHeight={GRID_ROW_HEIGHT}
        autoHeight
        getRowId={r => r.roleId}
        hideFooter
        selectionModel={(roles || [])
          .filter(r => r.roleId === roleId)
          .map(r => r.roleId)}
        onSelectionModelChange={newSelection => {
          setRoleId(newSelection[0]?.toString())
        }}
      />
      <Search
        keywordTypeItems={searchTypes}
        handleSearch={handleSearch}
        conditionKey={conditionKey}
      />
      <CustomDataGrid
        page={page}
        classes={classes}
        rows={data?.content}
        columns={authorizationColumns}
        rowCount={data?.totalElements}
        paginationMode="server"
        pageSize={GRID_PAGE_SIZE}
        onPageChange={handlePageChange}
        getRowId={r => r.authorizationNo}
        checkboxSelection
        disableSelectionOnClick
      />
      <CustomButtons buttons={[saveButton, deleteButton]} />
      <CustomAlert
        contentText={customAlert.message}
        open={customAlert.open}
        handleAlert={() => setCustomAlert({ open: false })}
      />
    </div>
  )
}

export const getServerSideProps: GetServerSideProps = async ({ query }) => {
  const roleId = query.roleId as string

  let roles: IRole[] = []
  let initRoleId = ''

  try {
    const result = await roleService.searchAll()
    if (result) {
      roles = result.data

      if (roles) {
        if (roleId) {
          initRoleId = roles.find(m => m.roleId === roleId).roleId
        }
        if (!initRoleId) {
          initRoleId = roles.find(m => m).roleId
        }
      }
    }
  } catch (error) {
    console.error(`role list getServerSideProps error ${error.message}`)
  }

  return {
    props: {
      roles,
      initRoleId,
    },
  }
}

export default RoleAuthorization
