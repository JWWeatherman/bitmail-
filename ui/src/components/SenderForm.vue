<template>
  <b-jumbotron id="sender-form" header="Bitcoin Mail Snail" lead="Send some coins to a friend..." >
    <b-form @submit="onSubmit" @reset="onReset">
      <b-form-group id="exampleInputGroup1"
                    label-for="exampleInput1"
                    description="It is really important that this email is correct.">
        <b-form-input id="exampleInput1"
                      type="email"
                      v-model="form.recipientEmail"
                      required
                      placeholder="Recipient email address">
        </b-form-input>
      </b-form-group>
      <b-form-group id="exampleInputGroup2"
                    label-for="exampleInput2"
      >
        <b-form-input id="exampleInput2"
                      type="email"
                      v-model="form.senderEmail"
                      placeholder="Your email address"
                      :readonly="form.remainAnonymous">
        </b-form-input>
        <b-form-group id="exampleGroup4">
          <b-form-checkbox v-model="form.remainAnonymous" value="that">Remain anonymous</b-form-checkbox>
        </b-form-group>
      </b-form-group>
      <b-form-group id="exampleInputGroup3"
                    label="Brief message to recipient:"
                    label-for="exampleInput3">
        <b-form-input id="exampleInput3"
                      type="text"
                      v-model="form.senderMessage"
                      placeholder="Message to recipient">
        </b-form-input>
      </b-form-group>
      <b-button type="submit" variant="primary">Submit</b-button>
      <b-button type="reset" variant="danger">Reset</b-button>
    </b-form>
  </b-jumbotron>
</template>
<script>
  export default {
    name: 'sender-form',
    props: ['form', 'onSubmit', 'onReset']
  }
</script>
<style>
</style>
