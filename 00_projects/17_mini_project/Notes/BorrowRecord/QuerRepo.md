### Get Borrow Records by return date == null and Memebr id

List<BorrowRecord> findByBorrowTransactionMemberIdAndReturnDateIsNull(Long memberId);

SELECT br.*
FROM borrow_record br
JOIN borrow_transaction bt
    ON br.borrow_transaction_id = bt.id
WHERE bt.member_id = ?
  AND br.return_date IS NULL;


  How Spring interprets it
BorrowRecord
    ↓ borrowTransaction
BorrowTransaction
    ↓ member
Member
    ↓ id

and adds the condition

returnDate IS NULL