import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    // 首页
    { path: '/', redirect: '/home' },
    { path: '/home', name: 'Home', component: () => import('../views/NewHome.vue') },

    // 用户相关
    { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
    { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
    { path: '/profile', name: 'Profile', component: () => import('../views/Profile.vue') },

    // 商城相关
    { path: '/shop', name: 'Shop', component: () => import('../views/Shop.vue') },
    { path: '/product/:id', name: 'ProductDetail', component: () => import('../views/ProductDetail.vue') },
    { path: '/cart', name: 'Cart', component: () => import('../views/Cart.vue') },
    { path: '/checkout', name: 'Checkout', component: () => import('../views/Checkout.vue') },
    { path: '/orders', name: 'Orders', component: () => import('../views/Orders.vue') },

    // 地址管理
    { path: '/address', name: 'AddressList', component: () => import('../views/AddressList.vue') },
    { path: '/address/add', name: 'AddressAdd', component: () => import('../views/AddressEdit.vue') },
    { path: '/address/edit/:id', name: 'AddressEdit', component: () => import('../views/AddressEdit.vue') },

    // 植物识别
    { path: '/identify', name: 'PlantIdentify', component: () => import('../views/PlantIdentify.vue') },
    { path: '/plant/:id', name: 'PlantDetail', component: () => import('../views/PlantDetail.vue') },

    // 管理后台（需要管理员权限）
    {
        path: '/admin',
        name: 'Admin',
        component: () => import('../views/admin/AdminLayout.vue'),
        meta: { requiresAdmin: true },
        children: [
            { path: '', redirect: '/admin/dashboard' },
            { path: 'dashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { requiresAdmin: true } },
            { path: 'products', component: () => import('../views/admin/ProductManage.vue'), meta: { requiresAdmin: true } },
            { path: 'plants', component: () => import('../views/admin/Plants.vue'), meta: { requiresAdmin: true } },
            { path: 'orders', component: () => import('../views/admin/Orders.vue'), meta: { requiresAdmin: true } },
            { path: 'tasks', name: 'AdminTasks', component: () => import('../views/admin/TasksManage.vue'), meta: { requiresAdmin: true } },
            { path: 'points-products', component: () => import('../views/admin/PointsProductManage.vue'), meta: { requiresAdmin: true } },
            { path: 'coupons', component: () => import('../views/admin/CouponManage.vue'), meta: { requiresAdmin: true } },
        ]
    },

    { path: '/history', name: 'History', component: () => import('../views/History.vue') },
    { path: '/gallery', name: 'PlantGallery', component: () => import('../views/PlantGallery.vue') },
    { path: '/browse-history', name: 'BrowseHistory', component: () => import('../views/BrowseHistory.vue') },
    { path: '/passport', name: 'PlantPassport', component: () => import('../views/PlantPassport.vue') },
    { path: '/tasks', name: 'Tasks', component: () => import('../views/Tasks.vue') },
    { path: '/coupon', name: 'Coupon', component: () => import('../views/Coupon.vue') },
    { path: '/points-mall', name: 'PointsMall', component: () => import('../views/PointsMall.vue') }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    const userInfoStr = localStorage.getItem('userInfo')
    const userInfo = userInfoStr ? JSON.parse(userInfoStr) : {}
    const userRole = userInfo.role || 'user'

    // 需要登录才能访问的页面
    const authPages = ['/profile', '/cart', '/checkout', '/orders', '/address', '/address/add']

    // 检查是否需要登录
    const requiresAuth = authPages.includes(to.path) || to.path.startsWith('/admin')

    // 需要登录但未登录
    if (requiresAuth && !token) {
        next('/login')
    }
    // 需要管理员权限
    else if (to.path.startsWith('/admin') && userRole !== 'admin') {
        next('/login')
    }
    else {
        next()
    }
})

export default router