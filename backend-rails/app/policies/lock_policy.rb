class LockPolicy < ApplicationPolicy
  def initialize(user, lock)
    @user = user
    @lock = lock
  end

  def index?
    true # as this method will only return locks that belongs to the user
  end

  def show?
    # allow if user has any relation to this lock
    @user.locks.exists?(@lock.id)
  end

  def create?
    true # everyone can create a new lock
  end

  def update?
    # only owner can update locks
    get_lock_access&.owner?
  end

  def destroy?
    # same as update
    update?
  end

  private

  def get_lock_access
    @user.lock_accesses.where(lock_id: @lock.id).first
  end
end
