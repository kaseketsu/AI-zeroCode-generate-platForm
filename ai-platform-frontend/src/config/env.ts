

// API 基础地址, 打包后用配置地址
import { codeGenTypeEnums } from '@/utils/codeGenType.ts'

export const API_BASE_URL =  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8123/api';

// 暂时用作本地启动
export const HOST_URL = 'http://localhost';

// 静态资源前缀
export const STATIC_BASE_URL = API_BASE_URL + '/static';

/**
 * 获得静态基础资源
 * @param codeGenType 代码生成类型
 * @param appId appId
 */
export const getStaticBaseUrl = (codeGenType: any, appId: string) => {
  const baseUrl =  `${STATIC_BASE_URL}/${codeGenType}_${appId}/`;
  if (codeGenType === codeGenTypeEnums.VUE_PROJECT) {
    console.log(codeGenType)
    return `${baseUrl}dist/index.html`;
  }
  return baseUrl
}

/**
 * 打开 nginx 部署页面
 * @param deployKey
 */
export const getDeployUrl = (deployKey: string | number | undefined) => {
  return `${HOST_URL}/${deployKey}`;
}
