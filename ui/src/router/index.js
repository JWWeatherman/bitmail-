import Vue from 'vue'
import Router from 'vue-router'
import VueResource from 'vue-resource'

import Main from '../components/Main.vue'
import Sender from '../components/Sender.vue'
import Recipient from '../components/Recipient.vue'

Vue.use(Router)
Vue.use(VueResource)

export default new Router({
  routes: [
    {
      path: '/sender',
      name: 'Sender',
      component: Sender
    },
    {
      path: '/recipient/:email?',
      name: 'Recipient',
      component: Recipient
    },
    {
      path: '/',
      name: 'Main',
      component: Main
    },
    {
      path: '*',
      redirect: '/'
    }
  ]
})
