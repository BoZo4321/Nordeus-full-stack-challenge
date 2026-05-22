using System.Collections.Generic;
using System.Linq;
using UnityEngine;
using UnityEngine.UI;

public class MoveManagementManager : MonoBehaviour
{
    [Header("Lists")]
    [SerializeField] private Transform learnedMovesContainer;
    [SerializeField] private GameObject moveButtonPrefab;

    [Header("Controls")]
    [SerializeField] private Button confirmButton;
    [SerializeField] private Button closeButton;
    [SerializeField] private Text feedbackText;
    [SerializeField] private Text equippedCountText;

    private List<string> _selectedIds = new();
    private HeroSummaryView _hero;

    void OnEnable()
    {
        _hero = GameManager.Instance.CurrentRun.hero;
        _selectedIds = _hero.equippedMoves?.Select(m => m.id).ToList() ?? new List<string>();
        SetFeedback("");
        BuildMoveList();

        confirmButton.onClick.RemoveAllListeners();
        confirmButton.onClick.AddListener(SaveMoves);

        closeButton.onClick.RemoveAllListeners();
        closeButton.onClick.AddListener(() => gameObject.SetActive(false));
    }

    private void BuildMoveList()
    {
        foreach (Transform child in learnedMovesContainer)
            Destroy(child.gameObject);

        var moves = _hero.learnedMoves ?? new Move[0];
        foreach (var move in moves)
        {
            var obj = Instantiate(moveButtonPrefab, learnedMovesContainer);
            var label = obj.GetComponentInChildren<Text>();
            var btn = obj.GetComponent<Button>();
            var moveId = move.id;

            UpdateMoveButton(label, move, _selectedIds.Contains(moveId));
            btn.onClick.AddListener(() =>
            {
                ToggleMove(moveId);
                UpdateMoveButton(label, move, _selectedIds.Contains(moveId));
            });
        }

        UpdateCountDisplay();

        Canvas.ForceUpdateCanvases();
        var scrollRect = learnedMovesContainer.GetComponentInParent<ScrollRect>();
        if (scrollRect != null) scrollRect.verticalNormalizedPosition = 1f;
    }

    private void UpdateMoveButton(Text label, Move move, bool selected)
    {
        label.text = (selected ? "[EQUIPPED] " : "") + move.name;
    }

    private void ToggleMove(string moveId)
    {
        SetFeedback(""); // obriši stari feedback kad korisnik menja selekciju
        if (_selectedIds.Contains(moveId))
        {
            _selectedIds.Remove(moveId);
        }
        else
        {
            if (_selectedIds.Count >= 4)
            {
                SetFeedback("Maximum 4 moves equipped!");
                return;
            }
            _selectedIds.Add(moveId);
        }
        UpdateCountDisplay();
    }

    private void UpdateCountDisplay()
    {
        equippedCountText.text = "Equipped: " + _selectedIds.Count + " / 4";
    }

    private void SaveMoves()
    {
        if (_selectedIds.Count == 0)
        {
            SetFeedback("Equip at least 1 move.");
            return;
        }

        StartCoroutine(ApiClient.Instance.Put<EquipMovesResponse>(
            "/runs/" + GameManager.Instance.CurrentRunId + "/hero/moves",
            new EquipMovesRequest { moveIds = _selectedIds.ToArray() },
            response =>
            {
                GameManager.Instance.CurrentRun.hero.equippedMoves = response.equippedMoves;
                GameManager.Instance.CurrentRun.hero.learnedMoves = response.learnedMoves;
                SetFeedback("Moves saved!");
            },
            error => SetFeedback("Error: " + error)
        ));
    }

    private void SetFeedback(string msg)
    {
        if (feedbackText != null) feedbackText.text = msg;
    }
}