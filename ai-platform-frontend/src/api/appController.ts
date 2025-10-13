// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 POST /app/admin/delete/id */
export async function deleteAppByIdAdmin(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/app/admin/delete/id', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /app/admin/get/detail/id */
export async function getAppDetailByIdAdmin(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAppDetailByIdAdminParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseAppVO>('/app/admin/get/detail/id', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /app/admin/get/detail/page */
export async function getAppDetailByPageAdmin(
  body: API.AppQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAppVO>('/app/admin/get/detail/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /app/admin/update */
export async function updateAppAdmin(
  body: API.AppUpdateRequestAdmin,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/app/admin/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /app/user/add */
export async function addApp(body: API.AppAddRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/app/user/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /app/user/chat/gen */
export async function chatToGen(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.chatToGenParams,
  options?: { [key: string]: any }
) {
  return request<API.ServerSentEventString[]>('/app/user/chat/gen', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /app/user/delete */
export async function deleteAppById(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/app/user/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /app/user/deploy */
export async function deploy(body: API.AppDeployRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/app/user/deploy', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /app/user/get/good/app */
export async function getGoodAppDetail(
  body: API.AppQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageAppVO>('/app/user/get/good/app', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /app/user/get/id */
export async function getAppById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAppByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseAppVO>('/app/user/get/id', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /app/user/get/my/app */
export async function getMyAppDetail(body: API.AppQueryRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponsePageAppVO>('/app/user/get/my/app', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /app/user/update */
export async function updateApp(body: API.AppUpdateRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/app/user/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
