

// API 基础地址, 打包后用配置地址
export const API_BASE_URL =  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8123/api';

// 静态资源前缀
export const STATIC_BASE_URL = API_BASE_URL + '/static';

/**
 * 获得静态基础资源
 * @param codeGenType 代码生成类型
 * @param appId appId
 */
export const getStaticBaseUrl = (codeGenType: any, appId: string) => {
  return `${STATIC_BASE_URL}/${codeGenType}_${appId}/`;
}
