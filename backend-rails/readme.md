# Sharelock Backend

The backend is responsible for providing APIs to store data for locks, enforce user permission model, log user activities, and more. Written with Ruby on Rails v7.

API documentation can be found [here](/backend-rails/docs/open-api-docs.yaml).

## Requirements

- Ruby 3.1
- Postgres 14

## Build Instruction

```zsh
cd backend-rails

# install dependencies
bundle

# prepare environment files
cp .env.example .env

# migrate database
rails db:migrate

# start the development server
rails s
```

Then you can visit https://localhost:3000

## Deployment

This repo is set to deploy to Heroku on every push to the master branch. The configuration of the automated deployment is located [here](https://github.com/chonhao/sharelock/blob/master/.github/workflows/deploy.yml)

But in case manual deployment is needed, the procedure is as follows:

```zsh
# login with your heroku account
heroku login

# add heroku as git remote
heroku git:remote -a sharelock

# push to the newly added remote
git push heroku master

# migrate database if necessary
heroku run -a sharelock "rails db:migrate"
```
