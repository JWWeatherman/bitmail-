<template>
    <div>
        <h1>Recipient</h1>
        <div>{{ email }}</div>
    </div>
</template>
<script>
    export default {
      name: 'recipient',
      mounted () {
        this.email = location.href
      },
      data () {
        return {
          email: ''
        }
      }
    }
</script>
