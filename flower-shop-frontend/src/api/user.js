import request from '@/utils/request'

export const loginAPI = (data) => request.post('/user/login', data)
export const registerAPI = (data) => request.post('/user/register', data)
export const getUserInfoAPI = () => request.get('/user/info')
export const updateProfileAPI = (data) => request.put('/user/profile', data)
export const changePasswordAPI = (data) => request.put('/user/password', data)
export const changePhoneAPI = (phone) => request.put('/user/phone', { phone })