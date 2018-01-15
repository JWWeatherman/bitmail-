<template>
    <div id="sender">
      <sender-form v-if="showForm" :form="form" :onSubmit="onSubmit" :onReset="onReset"></sender-form>
      <sender-send v-else :publicKeyAddress="serverResponse.publicKeyAddress" :serverResponse="serverResponse" :onReset="onReset"></sender-send>
    </div>
</template>

<script>
  import SenderForm from './SenderForm.vue'
  import SenderSend from './SenderSend.vue'

  export default {
    name: 'sender',
    data () {
      return {
        form: {
          recipientEmail: '',
          senderEmail: '',
          senderMessage: '',
          remainAnonymous: false
        },
        showForm: true,
        serverResponse: null
      }
    },
    methods: {
      onSubmit (evt) {
        evt.preventDefault()
        this.$http.post('/v1/createWallet', this.form)
          .then(res => {
            this.showForm = false
            this.serverResponse = res.body
            this.resetForm()
            console.log(JSON.stringify(res.body, null, 4))
          })
          .catch(err => {
            this.serverResponse = err.body
          })
      },
      resetForm () {
        /* Reset our form values */
        this.form = {
          recipientEmail: '',
          senderEmail: '',
          senderMessage: '',
          remainAnonymous: false
        }
      },
      onReset (evt) {
        evt.preventDefault()
        this.resetForm()
        /* Trick to reset/clear native browser form validation state */
        this.showForm = false
        this.$nextTick(() => { this.showForm = true })
      }
    },
    components: {
      SenderForm,
      SenderSend
    }
  }
</script>
