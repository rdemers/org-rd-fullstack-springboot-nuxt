<!--
  * Copyright 2023; Réal Demers.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *    http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  -->
<template>
    <img class="vuetify-logo" alt="Vuetify Logo" src="/vuetify-logo.svg">
</template>

<script setup lang="ts">
</script>

<style scoped>
    .vuetify-logo {
        height: 180px;
        width: 180px;
        transform: rotateY(560deg);
        animation: turn 3.5s ease-out forwards 1s;
    }

    @keyframes turn {
        100% {
            transform: rotateY(0deg);
        }
    }
</style>
